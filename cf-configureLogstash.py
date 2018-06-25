#!/usr/bin/python

from boto.s3.connection import S3Connection
import boto.ec2
import requests
import sys
import os


def getTag( region,inst,tagname ):
    "get tag value"
    conn = boto.ec2.connect_to_region(region)
    res = conn.get_all_instances(filters={"instance-id":inst})
    instance = res[0].instances[0]
    try:
        myret = instance.tags[tagname]
    except:
        myret = 'UNKNOWN'
    return myret
# end getTag

def getMeta( field ):
    "getMeta: argument is field, return is value from local EC2 API"
    r = requests.get('http://169.254.169.254/latest/dynamic/instance-identity/document')
    try:
        data = r.json
        myret = data[field]
    except:
        data = r.json()
        myret = data[field]
    return myret
# end getMeta

# def pullAssets( paenv ):
#     if paenv.lower() == 'prd':
#         pabucket = 'elk-prd'
#     else:
#         pabucket = 'elk-devtest'
#     paconn = S3Connection()
#     try:
#         bucket = paconn.get_bucket(pabucket)
#     except:
#         print "Unable to connect to bucket ", pabucket, "\n"
#         print "environment tag is ", paenv, "\n"
#         print "Cloud logging will be disabled\n"
#         sys.exit(1)
#     keyLogstash = bucket.get_key('logstash/config/cf.logstash.conf.template')
#     keyLogstash.get_contents_to_filename('/tmp/cf.logstash.conf.template')
# # end pullAssets


def main():
    myinstance = getMeta('instanceId')
    myregion   = getMeta('region')
    myip       = getMeta('privateIp')
    myapp      = getTag(myregion,myinstance,'service')
    mystack    = getTag(myregion,myinstance,'aws:cloudformation:stack-name')
    myesprefix = 'search-test-framework-aflp576i7ywoka6ibyw6lfpl7e'
    if sys.argv[1].lower() == 'prd':
        myenv = 'prd'
    else:
        myenv = 'dev'
    if myenv.lower() == 'prd':
        mysyslog = 'prd-guestbook-syslog'
    else:
        mysyslog = 'int-guestbook-syslog'

    print sys.argv[0],': instance: ',myinstance
    print sys.argv[0],': region: ',myregion
    print sys.argv[0],': ipv4: ',myip
    print sys.argv[0],': app: ',myapp
    print sys.argv[0],': environment: ',myenv
    print sys.argv[0],': syslog-server: ',mysyslog
    print sys.argv[0],': myesprefix: ',myesprefix

    if not os.path.exists('/etc/logstash/conf.d/'):
        os.mkdir('/etc/logstash/conf.d/')
    f = open('/tmp/logstash.conf.template','r')
    o = open('/etc/logstash/conf.d/logstash.conf','w')
    for line in f:
        line = line.replace('INSTANCEID',myinstance,1)
        line = line.replace('APPLICATION',myapp,1)
        line = line.replace('IPV4',myip,1)
        line = line.replace('REGION',myregion,1)
        line = line.replace('ENVIR',myenv,1)
        line = line.replace('SYSLOG',mysyslog,1)
        line = line.replace('STACKNAME',mystack,1)
	line = line.replace('ESPREFIX',myesprefix,1)
        o.write(line)

    f.close()
    o.close()
# end main

if __name__ == "__main__":
    main()
