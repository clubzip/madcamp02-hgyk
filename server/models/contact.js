var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var contactSchema = new Schema({
    facebookID: String,
    contactList: [{name: String, dial: String}],
});

module.exports = mongoose.model('contact', contactSchema);