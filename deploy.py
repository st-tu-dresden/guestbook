__author__ = 'acook'

import os
import boto
import boto.exception
import boto.sns
import urllib2
import boto.s3.connection
import boto.s3.bucket
import time
import subprocess
import boto.ec2.instanceinfo
import boto.cloudformation
import shutil
import requests

ACCESS_KEY = 'AKIAINRSHZC4HMXRVKJA'
SECRET_KEY = '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'
egx_version = os.environ['version']
egx_component = os.environ['component']
egx_environment = os.environ['environment']
egx_region = os.environ['region']
egx_branchname = os.environ['branchname']
egx_buildnumber = os.environ['buildnumber']

class SNSMessage:
    def __init__(self,eventType,eventComponent,eventEnv,eventHost,region,version):
        self.eventType = eventType
        self.eventComponent = eventComponent
        self.eventEnv = eventEnv
        self.eventHost = eventHost
        self.awsRegion = region
        self.version = version
        self.snsConnection = boto.sns.connect_to_region(region_name=region)

    def sendMessage(self, msg, env):

        connection = self.snsConnection
        if env == 'PRD':
            awsaccountnumber = '326626545197'
        else:
            awsaccountnumber = '050190852521'

        attrib = { 'EventType' :
                   { 'data_type' : 'String',
                     'string_value' : self.eventType },
                'EventComponent' :
                   { 'data_type' : 'String',
                     'string_value' : self.eventComponent },
                'EventENV' :
                   { 'data_type' : 'String',
                     'string_value' : self.eventEnv },
                'InstanceID' :
                   { 'data_type' : 'String',
                     'string_value' : self.eventHost },
                'Version' :
                   { 'data_type' : 'String',
                     'string_value' : self.version },
        }

        post = connection.publish(topic='arn:aws:sns:' + self.awsRegion + ':' + awsaccountnumber + ':scaleEvent', message=msg, message_attributes=attrib)
        print post

class Deploy:
    def __init__(self, version, component, env, region, branchname, buildnumber):
        self.version = version
        self.component = component
        self.env = env
        self.region = region
        self.ec2Connection = boto.ec2.connect_to_region(region_name=region)
        self.branchname = branchname
        self.buildnumber = buildnumber

    @staticmethod
    def getartifact(signedurl):
        url = signedurl
        file_name = url.split('?')[0].split('/')[-1]
        u = urllib2.urlopen(url)

        with open(file_name, 'wb') as f:
            meta = u.info()
            file_size = int(meta.getheaders("Content-Length")[0])
            print "Downloading: %s Bytes: %s" % (file_name, file_size)

            file_size_dl = 0
            block_sz = 8192
            while True:
                bufferer = u.read(block_sz)
                if not bufferer:
                    break

                file_size_dl += len(bufferer)
                f.write(bufferer)
                status = r"%10d [%3.2f%%" % (file_size_dl, file_size_dl * 100. / file_size)
                status += chr(8) * (len(status) + 1)
                print status,
        f.close()
        os.rename(file_name, '/var/tmp/deploy/' + file_name)
        return file_name

    @staticmethod
    def stopTomcat():
        p1 = subprocess.Popen(["ps", "-fu", "tomcat"], stdout=subprocess.PIPE)
        p2 = subprocess.Popen(["awk", "{print ($2)}"], stdin=p1.stdout, stdout=subprocess.PIPE)
        subprocess.call(["service", "tomcat7", "stop"], shell=False)
        pid = p2.communicate()[0]

        print '\n'
        print 'Tomcat' + pid
        return pid

    @staticmethod
    def startTomcat():
        subprocess.call(["service", "tomcat7", "start"], shell=False)
        p1 = subprocess.Popen(["ps", "-fu", "tomcat"], stdout=subprocess.PIPE)
        p2 = subprocess.Popen(["awk", "{print ($2)}"], stdin=p1.stdout, stdout=subprocess.PIPE)

        pid = p2.communicate()[0]
        print '\n'
        print 'Tomcat ' + pid
        return pid

    @staticmethod
    def cleanTomcatDirs():
        tomcatDir = '/usr/share/tomcat7'
        if os.path.exists('/var/lib/tomcat7/webapps/ROOT'):
            shutil.rmtree('/var/lib/tomcat7/webapps/ROOT')
            shutil.rmtree(tomcatDir + '/work/Catalina')
            os.remove(tomcatDir + '/webapps/ROOT.war')
        else:
            print 'Nothing to clean...'

    @staticmethod
    def testCall(component):
        response = 0
        counter = 0
        deploymentComplete = False
        while response == 0 :
            try:
                if component == 'universal-engine':
                    siteTestCall = requests.get('http://localhost:8080/engine/ping', timeout=5)
                    response = siteTestCall.status_code
                else:
                    #siteTestCall = requests.get('http://localhost:8080', timeout=5)
                    #response = siteTestCall.status_code
                    print "Not testing since this is a UI deployment"
                    break
            except requests.RequestException, e:
                print e.args
                time.sleep(15)
                pass

            if response == 200:
                deploymentComplete = True
                print "Site is up and responding."
                break
            elif response >= 400:
                print "The site returned a " + str(response) + " error."
                break
            elif response >= 500:
                print "The site returned a " + str(response) + " error."
                break
            elif counter >= 6:
                print "The site didn't come up after 90 seconds."
                break

            counter += 1
            response += response

        return deploymentComplete

    def Deploy(self):
        version = self.version
        component = self.component
        env = self.env
        region = self.region
        branchname = self.branchname
        buildnumber = self.buildnumber

        metadata = requests.get('http://169.254.169.254/latest/meta-data/instance-id')
        instance_id = metadata.text
        print 'Creating deployment directories'

        if os.path.exists('/var/tmp/deploy'):
            shutil.rmtree('/var/tmp/deploy')

        os.mkdir('/var/tmp/deploy')
        os.chmod('/var/tmp/deploy', 0777)
        if os.path.exists('/var/lib/tomcat7/webapps'):
            os.chmod('/usr/share/tomcat7/webapps', 0777)
        else:
            os.mkdir('/usr/share/tomcat7/webapps')
            os.chmod('/usr/share/tomcat7/webapps', 0777)

        print 'Downloading EGX Artifacts'
        conn = boto.connect_s3(aws_access_key_id=ACCESS_KEY, aws_secret_access_key=SECRET_KEY)
        try:
            bucketLookup = conn.get_bucket('ghx-deployment')


            bucketPrefix = 'EGX/' + version + '/' + component + '-' + version + '-' + buildnumber + '-' + branchname + '.war'
            filename = component + '-' + version + '-' + buildnumber + '-' + branchname + '.war'
            EGX_key = bucketLookup.get_key(bucketPrefix)
            EGX_url = EGX_key.generate_url(3600, query_auth=True)
            self.getartifact(EGX_url)
        except boto.exception.S3PermissionsError:
            print "ERROR: There was a permissions issue with creating the bucket "
            raise
        except boto.exception.S3ResponseError:
            print "ERROR: Could not find the bucket name"
            raise
        except boto.exception.S3CopyError:
            print "ERROR: THere was problem with getting the Artifacts..."
            raise

        print 'Stopping Tomcat...'
        self.stopTomcat()

        print 'Cleaning out tomcat cache...'
        self.cleanTomcatDirs()

        print "Moving war file to /var/lib/tomcat7/webapps"
        os.rename('/var/tmp/deploy/' + filename, '/var/lib/tomcat7/webapps/ROOT.war')
        print 'Sleeping for 8 seconds'
        time.sleep(8)

        print 'Starting Tomcat...'
        self.startTomcat()
        
        testsite = self.testCall(component)
        
        if testsite:
            print "SUCCESS: Deployment Finished"
            message = SNSMessage('Deployment', component, env , instance_id, region, version)
            message.sendMessage('SUCCESS: Deployment of ' + component + version + ' is Complete', env)
        else:
            print "ERROR: Deployment Finish, but did not respond to a ping"
            #exit (1)

Deploy(egx_version,egx_component,egx_environment,egx_region,egx_branchname,egx_buildnumber).Deploy()
