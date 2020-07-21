var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var worktimeSchema = new Schema({
    facebookID: String,
    name: String,
    date: String,
    start: String,
    end: String
});

module.exports = mongoose.model('worktime', worktimeSchema);