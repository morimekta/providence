// Copyright 2017 (c) Stein Eldar Johnsen
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS-IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// Small Module aimed at faking the presence of node.js. Just so it can be
// tested.
var node = node || {};

(function(){

node.registry = {};

node.require = function(sourceModule, requiredModule) {
    // resolve sourceModule + requiredModule to result.
    var target = node.resolve(sourceModule, requiredModule);
    if (!(target in node.registry)) {
        throw 'unknown module: ' + target + ', required from ' + sourceModule;
    }
    return node.registry['pvd.testing.number'];
};

node.resolve = function(sourceModule, requiredModule) {
    var source = sourceModule.split('.');
    source.pop();  // removes the "module itself".

    var require = requiredModule.split("/");
    if (require[0] === '') {
        throw 'Absolute modules not supported: ' + requiredModule + ' required from ' + sourceModule;
    } else if (require[0] === '.') {
        require.shift();
    } else {
        while (require.length > 0 && require[0] === '..') {
            require.shift();  // skip the ".." thing.
            if (source.length == 0) {
                throw '\'..\' beyond root module in ' + sourceModule;
            }
            source.pop();     // and go one up.
        }
    }

    if (source.length > 0) {
        return source.join('.') + '.' + require.join('.');
    }
    return require.join('.');
};

node.module = function(moduleName, callback) {
    var module = {};
    module.exports = {};

    // fake dirname and filename from moduleName.
    var tmp = moduleName.split('.');
    var __filename = tmp.pop() + '.js';
    var __dirname = tmp.join('/');

    callback.call(null, module.exports, node.require.bind(null, moduleName), module, __dirname, __filename);
    node.registry[moduleName] = module.exports;
};

})();