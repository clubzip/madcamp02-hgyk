# Description

### This package contains three tabs, that is, Gallery tab, Contact tab, and Calendar tab.

## 0. Facebook Login
With Facebook login SDK, this app can identify the user.

## 1. Gallery tab
The first tab is a gallery of images of the app's own external storage. Click on the image in the gallery to see the enlarged picture. In this state, you can view the next image in slide motion. You can view the selection mode by holding down the image in the gallery for a while. Select the image in Select mode and press the camera-shaped floating button to delete it. (to be changed to a proper icon in the future) Pressing the camera-shaped floating button in the default mode instead of the select mode becomes the filming mode. Photos taken at this time are immediately reflected in the gallery. Photos taken in this app are not affected by other apps.

The communication between node.js server and this client is added. There's a MongoDB database which store the list of image file for particular user, and the files in list are stored at node.js server. When the client is started, it compare the its own file list in external storage and file list from DB. It downloads the added image file in DB from server, and it removes the removed image file in DB(maybe it has delete from other device which is logged in with same facebook account) from external storage.

When a picture is taken through camera, it request to update the file list in DB and upload the image file to server. This process is implemented with 'multer' and 'retrofit2'.



## 2. Contact tab
The Contact tab prints the list of contact data which is stored in database at the server.
User can add the contact information containing name, and mobile number by touching '+' button.
In each contact item activity, user can execute the dial and sms to the data. Additionally, user can remove the data from internal storage and the server.
The Edit button is not implemented yet.


This fragment contains custom listview. Thus, it contains 'ListViewAdapter' which extends BaseAdapter and 'ListViewItem'.
ListViewAdapter implements Filterable for searching function. Each ListViewItem has three private member variables that contain the information of name, mobile number. ListViewAdapter creates view for each row of custom listview using this data. Initially, these data are from JSON file at internal storage.


The addition and remove of contact data is implemented by rewriting the JSON file and also updating database. To prints the changed listview, the activities for addition and remove are start by calling startActivityForResult. And the Fragment renew itself in the method 'onActivityResult.'


*When the fragment is activated, the app check whether there is data that matches to facebookID(=userid) in the internal storage. If there is, it gets data from internal storage. Otherwise, it creates new document in the json file. And then get data from DB.(If there are no data in DB, create new document without item in contactList.)



## 3. Work time checker
User can record his start time and end time for particular work. The data is recorded in DB, and the app shows the ranking of coworkers. The ranking is based on start time and actual work time which is calculate by substracting end time and start time. The ranking list are implemented with custom listview.





