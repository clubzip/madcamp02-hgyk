// serverjs

// [LOAD PACKAGES]
var express     = require('express');
var app         = express();
var bodyParser  = require('body-parser');
var mongoose    = require('mongoose');
//////////////////////////
var multer, storage, path, crypto;
multer = require('multer')
path = require('path');
crypto = require('crypto');
/////////////////////////

// [ CONFIGURE mongoose ]

// CONNECT TO MONGODB SERVER
var db = mongoose.connection;
db.on('error', console.error);
db.once('open', function(){
    // CONNECTED TO MONGODB SERVER
    console.log("Connected to mongod server");
});

mongoose.connect('mongodb://localhost/db');

// DEFINE MODEL
var Book = require('./models/book');
var Contact = require('./models/contact');
var Image = require('./models/image');
var Worktime = require('./models/worktime');

// [CONFIGURE APP TO USE bodyParser]
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

// [CONFIGURE SERVER PORT]

var port = process.env.PORT || 80;

var storage = multer.diskStorage({
    destination: function(req, file, cb) {
        cb(null, './images/'+req.params.facebookID+'/');
    },
    filename: function(req, file, cb) {
      cb(null, file.originalname);
    }
  });

// [CONFIGURE ROUTER] -------------multer, path, crypto 추가
var router = require('./routes')(app, Book, Contact, Image, Worktime, multer, path, storage);



// [RUN SERVER]
var server = app.listen(port, function(){
 console.log("Express server has started on port " + port)
});

