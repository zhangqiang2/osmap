/*
 * 使用方法：casperjs openhub.js --url=https://www.openhub.net/p/Hadoop
 *
 * 其中 --url 是必选参数， 其它为可选参数
 *
 */


function customEcho(msg){
    var preStr = '*********************************************';
    casper.log(preStr, 'info');
    casper.echo('[python-pipe] ' + msg);
    casper.log(preStr, 'info');
}

function dateFromString(str) {
    var m = str.match(/(\d+)-(\d+)-(\d+)\s+(\d+):(\d+)/);
    return new Date(+m[1], +m[2] - 1, +m[3], +m[4], +m[5]).getTime();
}

var projectUrl='';
function checkParam(option, msg){
    if(casper.cli.has(option)){
        return casper.cli.get(option);
    } else{
        customEcho(msg);
        casper.exit(201);
    }
}
function parseParam(){
    projectUrl = checkParam('url', 'error: no url specified');
}

function getBaseDir(){
    var currentFile = require('system').args[3];
    var curFilePath = require('fs').absolute(currentFile).split('/');
    curFilePath.pop();
    return curFilePath.join('/');
}

basedir = getBaseDir();
time_out_value = 10000;
flag = true;

// settings for casper
casper = require("casper").create({
    viewportSize: {width: 800, height: 600},
    pageSettings: {
        loadImages: false,
        loadPlugins: false,
        proxy: 'http://10.43.163.32:2222',
        userAgent: 'Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0'
    },
    stepTimeout:time_out_value*2,
    onStepTimeout: function(message) {
        customEcho('error: step timeout, current config value: ' + time_out_value*2);
        casper.exit(255);
    },

    //clientScripts: [basedir + "/custom-script.js"],
    verbose:true,
    logLevel: "info"
    //logLevel: "debug"
});

parseParam();

var project_info = {
    'url': projectUrl,
    'locations': '',
    'foundation': '',
    'community' : '',
    'communityurl' : ''
};

function dumpResultInfo(){
    customEcho('result:' + JSON.stringify(project_info));
}

function nextPage(){
    if (flag){
        if(casper.exists('div.W_pages>a.next')){
            // goto next page
            casper.click('div.W_pages>a.next');
        }else{
            customEcho('we are now at the last page');
        }
    }
}

function reloadPage(){
    this.reload();
}

function onTimeOut(timeout, detail){
    customEcho(arguments.length);
    customEcho('info: timeout detail:' + JSON.stringify(detail));
    this.capture(basedir + '/' + require('system').pid + "-bo.png");
    customEcho('what a pity, maybe we will turn up the timeout value next time, please check the snapshot bo.png');
    customEcho('error: timeout, current config value: ' + timeout);
    casper.exit(255);
}

casper.start(projectUrl, function(){
    customEcho('info: we are trying to open url');
});

// get foundation and communityurl
casper.waitForSelector('a[href$="enlistments"]', function then(){
    customEcho('info: try to get foundation and community url');
    var infos = this.evaluate(function(){
        var result = {};
        var l = document.querySelector('a[href^="/orgs/"]');
        if(l) {
            result.foundation = l.textContent;
        } else {
            result.foundation = '';
        }
        var community = $('a:contains("Community")');
        if (community.length > 0) {
            url = community.attr('href');
            if (url.indexOf('/reviews/summary')<0) {
                result.communityurl= url;
            }else{
                result.communityurl= '';
            }
        } else {
            result.communityurl= '';
        }
        return result;
    });
    project_info.foundation = infos.foundation;
    project_info.communityurl = infos.communityurl;
    customEcho('info: success to get foundation/communityurl: ' +  infos.foundation + ';;  ' + infos.communityurl + '$$');
}, onTimeOut, time_out_value);

casper.thenBypassIf(function() {
    var needSkip = this.evaluate(function(){
        if ($('a:contains("Add a code location")').length > 0) {
            return true;
        }
        locationElement = $('a[href$="enlistments"]');
        if (locationElement.length === 1) {
            return locationElement.parent().next().text().trim();
        }
        return false;
    });

    if (typeof needSkip === 'string') {
        if (needSkip.indexOf('...') < 0) {
            customEcho('info: get location from home page, skip thenOpen');
            project_info.locations = needSkip;
            return true;
        } else {
            customEcho('info: get location from home page, but it contains ...  need thenOpen');
            return false;
        }
    }
    if (needSkip) {
        customEcho('info: no locations found, skip thenOpen');
    }
    return needSkip;
}, 2);

// get locations
casper.thenOpen(projectUrl + '/enlistments', function(){
    customEcho('info: try to get locations: ' +  this.getCurrentUrl());
    casper.waitForSelector('table tr.enlistment>td:first-child', function(){
        var locations = this.evaluate(function(){
            console.log('info: try to get locations');
            var result = [];
            var l = document.querySelectorAll('table tr.enlistment>td:first-child');
            if(l.length > 0) {
                for(var i=0; i<l.length; i++){
                    result.push(l[i].textContent.trim());
                }
            }
            return result.join();
        });
        project_info.locations = locations;
        customEcho('info: success to get locations: ' +  locations);

    }, onTimeOut, time_out_value);
});

casper.then(dumpResultInfo);

casper.run();

