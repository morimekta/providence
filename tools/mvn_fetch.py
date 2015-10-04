#!/usr/bin/python
__author__ = 'steineldar@zedge.net'

# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements. See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership. The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License. You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied. See the License for the
# specific language governing permissions and limitations
# under the License.
import sys, os, subprocess
from argparse import ArgumentParser

MVN_HOME = os.path.expanduser('~/.m2')
MVN_REPOSITORY = os.path.join(MVN_HOME, 'repository')
MVN_CENTRAL = 'http://repo1.maven.org/maven2'


def mvn_cached_file(artifact):
    '''
    :param artifact: The artifact to fetch
    :return: The absolute file path of the expected cached file path.
    '''
    parts = artifact.split(':')
    if not len(parts) in [3, 4]:
        raise NameError("")
    if len(parts) is 3:
        package, target, version = parts
        component = None
    else:
        package, target, version, component = parts
    v = version
    if '-SNAPSHOT' in version:
        v = version.split('-SNAPSHOT')[0]

    if component is None:
        file_name = '-'.join([target, v]) + '.jar'
    else:
        file_name = '-'.join([target, v, component]) + '.jar'

    file_path = os.path.join(
        MVN_REPOSITORY,
        os.path.join(*package.split('.')),
        target,
        version,
        file_name
    )

    return file_path


def run_fetch(repository, artifact, dest):
    '''
    :param repository: Repository URL to fetch from.
    :param artifact: Atrifact ID to fetch.
    :param dest: Destination cache file.
    :return: 0 if the command succeeded.
    :raises CalledProcessError: If fetch failed for any reason.
    '''
    fetch = ['mvn',
             'org.apache.maven.plugins:maven-dependency-plugin:2.1:get',
             '-DrepoUrl=' + repository,
             '-Dtransitive=false',
             '-Dartifact=' + artifact]
    subprocess.check_call(fetch)


def main(argv):
    parser = ArgumentParser()
    parser.add_argument('-r', '--repository', default=MVN_CENTRAL, help="The maven repository URL")
    parser.add_argument('-a', '--artifact', required=True, help="The maven artifact. (?<package>):(?<id>):(?<version>)(:(?<component>))?")
    parser.add_argument('-d', '--dest', required=True, help="The destination file path.")
    parser.add_argument('-s', '--src', help="Source path within target file to prune.")
    args = parser.parse_args(argv)

    cache_file = mvn_cached_file(args.artifact)
    if not os.path.exists(cache_file):
        run_fetch(args.repository, args.artifact, cache_file)
        if not os.path.exists(cache_file):
            raise IOError("Unable to download file " + cache_file)

    dest_path = os.path.dirname(args.dest)
    if dest_path is not None and len(dest_path) > 0 and not os.path.isdir(dest_path):
        mkdir = ['mkdir', '-p', dest_path]
        subprocess.check_call(mkdir)

    copy = ['cp', cache_file, args.dest]
    subprocess.check_call(copy)


if __name__ == "__main__":
    main(sys.argv[1:])
