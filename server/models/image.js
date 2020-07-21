var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var imageSchema = new Schema({
    facebookID: String,
    imageList: [String]
});

module.exports = mongoose.model('image', imageSchema);