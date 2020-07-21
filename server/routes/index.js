module.exports = function(app, Book, Contact, Image, Worktime, multer, path, storage)
{

    // GET ALL BOOKS
    app.get('/api/books', function(req,res){
        Book.find(function(err, books){
            if(err) return res.status(500).send({error: 'database failure'});
            res.json(books);
        })
    });

    // // GET SINGLE BOOK from facebook id
    // app.get('/api/books/:fbid', function(req, res){
    //     Book.findOne({fbid: req.params.fbid}, function(err, book){
    //         console.log("here")
    //         if(err) return res.status(500).json({error: err});
    //         if(!book) return res.status(404).json({error: 'book not found'});
    //         res.json(book);
    //     })
    // });

    // GET SINGLE BOOK
    app.get('/api/books/:book_id', function(req, res){
        Book.findOne({_id: req.params.book_id}, function(err, book){
            if(err) return res.status(500).json({error: err});
            if(!book) return res.status(404).json({error: 'book not found'});
            res.json(book);
        })
    });

    // GET BOOK BY AUTHOR
    app.get('/api/books/author/:author', function(req, res){
        Book.find({author: req.params.author}, {_id: 0, name: 1, published_date: 1},  function(err, books){
            if(err) return res.status(500).json({error: err});
            if(books.length === 0) return res.status(404).json({error: 'book not found (author)'});
            res.json(books);
        })
    });

    // CREATE BOOK
    app.post('/api/books', function(req, res){
        var book = new Book();
        book.name = req.body.name;
        book.author = req.body.author;
        book.published_date = new Date(req.body.published_date);

        book.save(function(err){
            if(err){
                console.error(err);
                res.json({result: 0});
                return;
            }

            res.json({result: 1});

        });
    });

    // UPDATE THE BOOK
    app.put('/api/books/:book_id', function(req, res){
        Book.update({ _id: req.params.book_id }, { $set: req.body }, function(err, output){
            if(err) res.status(500).json({ error: 'database failure' });
            console.log(output);
            if(!output.n) return res.status(404).json({ error: 'book not found' });
            res.json( { message: 'book updated' } );
        })
    });


    // DELETE BOOK
    app.delete('/api/books/:book_id', function(req, res){
        Book.remove({ _id: req.params.book_id }, function(err, output){
            if(err) return res.status(500).json({ error: "database failure" });

            /* ( SINCE DELETE OPERATION IS IDEMPOTENT, NO NEED TO SPECIFY )
            if(!output.result.n) return res.status(404).json({ error: "book not found" });
            res.json({ message: "book deleted" });
            */

            res.status(204).end();
        })
    });

    // CREATE NEW CONTACT
    app.post('/api/contacts', function(req, res){
        console.log("create new contact");
        var contact = new Contact();
        contact.facebookID = req.body.facebookID;
        contact.contactList = req.body.contactList;
        contact.save(function(err){
            if(err){
                console.error(err);
                res.json({result: 0});
                console.log("fail");
                return;
            }

            console.log("success");

            res.json({result: 1});

        });
    });

    // GET CONTACTLIST BY FACEBOOKID
    app.get('/api/contacts/facebookID/:facebookID', function(req, res){
        console.log("get contactlist by facebookid1");
        Contact.find({facebookID: req.params.facebookID}, {_id: 0, facebookID: 1, contactList: 1},  function(err, contact){
            console.log("get contactlist by facebookid2");
            if(err) {
                console.log("status 500");
                return res.status(500).json({error: err});
            }
            if(contact.length === 0) {
                console.log("nothing");
                return res.status(404).json({error: 'contactList not found (GET BY FACEBOOKID)'});
            }
            console.log("success")
            res.json(contact);
        })
    });

    // ADD {key, value} to CONTACTLIST
    app.put('/api/contacts/add/facebookID/:facebookID', function(req, res){
        console.log("add key, value to contact list1");
        Contact.update({ facebookID: req.params.facebookID }, { $push: {contactList: req.body} }, function(err, output){
            console.log("add key, value to contact list2");
            if(err) res.status(500).json({ error: 'database failure' });
            console.log(output);
            if(!output.n) return res.status(404).json({ error: 'contactList not found' });
            res.json( { message: 'contactList updated' } );
        })
    });

    // DELETE {key, value} from CONTACTLIST by FACEBOOKID and KEY(NAME)
    app.post('/api/contacts/delete/facebookID/:facebookID', function(req, res){
        console.log("delete key, value from contact list");
        console.log(req.body);
        Contact.update({ facebookID: req.params.facebookID }, { $pull: {contactList: req.body} }, function(err, output){ ///////////여기바꾸기
            if(err) res.status(500).json({ error: 'database failure' });
            console.log(output);
            // if(!output.n) return res.status(404).json({ error: 'contactList not found' });
            res.json( { message: 'contactList updated' } );
        })
    });


    // DELETE CONTACT
    app.delete('/api/contacts/facebookID/:facebookID', function(req, res){
        Contact.remove({ facebookID: req.params.facebookID }, function(err, output){
            if(err) return res.status(500).json({ error: "database failure" });

            /* ( SINCE DELETE OPERATION IS IDEMPOTENT, NO NEED TO SPECIFY )
            if(!output.result.n) return res.status(404).json({ error: "book not found" });
            res.json({ message: "book deleted" });
            */

            res.status(204).end();
        })
    });

    // CREATE NEW GALLERY
    app.post('/api/images', function(req, res){
        var image = new Image();
        image.facebookID = req.body.facebookID;
        image.imageList = req.body.imageList;
        image.save(function(err){
            if(err){
                console.error(err);
                res.json({result: 0});
                return;
            }

            res.json({result: 1});

        });
    });

    // GET IMAGELIST BY FACEBOOKID
    app.get('/api/images/facebookID/:facebookID', function(req, res){
        // console.log("image_get");
        Image.find({facebookID: req.params.facebookID}, {_id: 0, facebookID: 1, imageList: 1},  function(err, image){
            console.log("aaaaaaaaaaaaaaaaaaaa");
            if(err) return res.status(500).json({error: err});
            if(image.length === 0) return res.status(404).json({error: 'image not found'});
            console.log("cccccccccccc");
            res.json(image);
            //Send image files which are listed in the Imagelist
        })
    });
    // GET IMAGE BY FACEBOOKID AND IMAGE FILE NAME
    app.post('/api/images/getimage/facebookID/:facebookID', function(req, res){
        console.log("get image api running");
        var fbid = req.params.facebookID;
        var filename = "../images/"+fbid+"/"+req.body.imageFilename;
        // var path = require("path"); -- 모듈에서 받아옴
        console.log(filename);
        res.sendFile(path.join(__dirname,filename));        
    });
    
    //Android client upload a image file to this server
    app.post( "/:facebookID/upload", multer({ storage: storage}).single('upload'), function(req, res) {
        Image.update({facebookID: req.params.facebookID}, {$push: { imageList : req.file.filename}}, function(err, output) {
            console.log(req.file);
            console.log(req.body);
            res.redirect("../images/"+ req.params.facebookID + '/' + req.file.filename);
            console.log(req.file.filename);
            return res.status(200).end();
        })
    });

    //Android client request to create new document for new user
    app.get( "/api/images/create/facebookID/:facebookID", function(req, res) {
        var image = new Image();
        image.imageList = [];
        image.facebookID = req.params.facebookID;
        

        image.save(function(err){
            if(err){
                console.error(err);
                res.json({result: 0});
                return;
            }
            res.json({result: 1});
        });
        
        var fs = require('fs');
        var dir = path.join(__dirname, '../images',req.params.facebookID);

        if(!fs.existsSync(dir)){
            fs.mkdirSync(dir);
        }
    });
    // Delete image file name from list and delete image file in directory
    app.post('/api/images/delete/facebookID/:facebookID', function(req, res){
        console.log("image_delete");
        // console.log(req.body.imageList);
        var list_wo_bracket = req.body.imageList.slice(1,-1);
        list_wo_bracket = list_wo_bracket.replace(/ /gi,"");
        var imagelist = list_wo_bracket.split(",");
        console.log(imagelist);
        Image.update({facebookID: req.params.facebookID},{$pull: { imageList: { $in: imagelist } } }, function(err, output) {
            var fs = require('fs');
            for( a of imagelist ){
                var path = __dirname+ '/../images/'+req.params.facebookID+'/'+a;
                fs.unlinkSync(path);
            }
            console.log("1 done");
        });
        console.log("2 done");
        
    });

    // CREATE Document when work
    app.post('/api/work/:facebookID', function(req, res){
        var worktime = new Worktime();
        worktime.facebookID = req.params.facebookID;
        worktime.name = req.body.name;
        worktime.date = req.body.date;
        worktime.start = req.body.start;
        console.log("enter here");

        worktime.save(function(err){
            if(err){
                console.error(err);
                res.json({result: 0});
                return;
            }
            console.log("work document created");

            res.json({result: 1});
        });
    });

    // UPDATE Document when leave
    app.post('/api/leave/:facebookID', function(req, res){
        Worktime.update({ facebookID: req.params.facebookID, date: req.body.date }, { $set: {end: req.body.leave} }, function(err, output){
            if(err) res.status(500).json({ error: 'database failure' });
            var opstr = output.toString();
            console.log(opstr.substring(opstr.indexOf("start")+8,opstr.indexOf("start")+14));
            if(!output.n) return res.status(404).json({ error: 'ID not found' });
            res.json( { message: 'work time updated' } );
        })
    });

    // POST work start ranking of the day
    app.post('/api/work/get/:date', function(req, res){
        Worktime.find({date: req.params.date}, {_id: 0, name: 1, start: 1, end: 1}, function(err, worktimes){
            if(err) return res.status(500).json({error: err});
            if(worktimes.length === 0) return res.status(404).json({error: 'book not found (author)'});
            res.json(worktimes);
        })
    });

    // Find work history
    app.post('/api/work/get/:date/:facebookID', function(req, res){
        console.log("hhhhhhhherererere");
        Worktime.find({date: req.params.date, facebookID: req.params.facebookID}, {_id: 0, name: 1, start: 1, end:1}, function(err, worktimes){
            console.log("aaaaaaaaaaaaa");
            console.log(req.params.date);
            console.log(req.params.facebookID);
            if(err) return res.status(500).json({error: err});
            
            console.log(worktimes);
            if(worktimes.length === 0) return res.status(404).json({error: 'book not found (author)'});
            res.json(worktimes);
        })
    });
}