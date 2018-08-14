/**
 * @fileoverview serverServer.js is the actual HTML server, based on e
 * TODO: write to a file or database
 */
'use strict';
var zoa = require('./zoa');



/**
 * @constructor
 */
zoa.Server = function (config, logger) {
  this.expressModule = require('express');
  this.fsModule = require('fs');
  this.pathModule = require('path');
  var url = require('url');

  this.dirName = process.cwd();
  this.ipList = config.getIpAddressList();
  this.logger = logger;
  this.port = config.getPort();
  this.webRoot = config.getWebRoot();

  this.express = this.expressModule();  // set up the express html server
  var scope = this;
  var proxy = require('express-http-proxy');

  this.express.use('/column_matcher_ws', proxy('52.52.67.126:5000', {
    forwardPath: function(req, res) {
      return require('url').parse(req.url).path;
    }
  }));

  scope.express.get('/save_data',function (req, res, next) {
    console.log('get data', req);
    console.log('get data', req.param('x'));

    var url_parts = url.parse(req.url, true);
    var query = url_parts.query;
    console.log(url_parts.query);

    scope.fsModule.writeFile('helloworld2.txt', 'Hello World!!', function (err) {
      if (err) return console.log(err);

      console.log('Hello World > helloworld.txt');
    });
  })
  this.setUpRoutes();  // a route is access to a page or cmd
  // start the html server listening for requests
  this.express.listen(this.port, this.handleServerListening.bind(this));
};


/**
 * A route is a server request for a page or command. This also logs
 * requests and handles any bad requests (e.g. 404). The order of the
 * route handling is important.
 */
zoa.Server.prototype.setUpRoutes = function() {
  // always do this route: log the request
  this.express.use(function(req, res, next) {
     var msg = [req.method, ' received: ', req.url].join('');
     this.logger.log(msg);
     next();
  }.bind(this));

  // map index.htm to index.html
  this.express.get('/index.htm', function(req, res) {
    res.sendFile(self.pathModule.normalize([this.dirName, 
                                            this.webRoot, 
                                            'index.html'].join('/')));
  }.bind(this));

  // route: handle static file requests
  this.express.use(this.expressModule.static(this.webRoot));

  // route: display 404 page
  this.express.use(function(req, res) {
    res.status(404).send('404 - not found');
  }.bind(this));

  // route: handle all other errors. This should appear AFTER all other routes
  this.express.use(function(err, req, res, next) {
    console.error(err.stack);
    this.logger.log(err.stack);
    res.status(500).send('500 - server error');
  }.bind(this));
};


/**
 * When the server starts listening, update the subtitle with the ip
 * address and log the start.
 */
zoa.Server.prototype.handleServerListening = function() {
  // build a string from the list of ip addresses
  var ipCount = this.ipList.length;
  for (var i = 0; i < ipCount; ++i) {
    this.ipList[i] = [this.ipList[i], ':', this.port].join('');
  }
  var ipString = this.ipList.join(', ');

  var msg = ['server started on:', ipString].join(' ');
  this.logger.log(msg);
  this.logger.log('Press ^C^C to stop the server');
};

module.exports = zoa.Server;
