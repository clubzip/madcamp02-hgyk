const express = require('express');
const app = express();
let users = [
  {
    id: 1,
    name: 'alice'
  },
  {
    id: 2,
    name: 'bek'
  },
  {
    id: 3,
    name: 'chris'
  }
]

//클라이언트로 부터 get메서드로 /server쪽에 요청이 오면 다음을 실행.
app.get('/server', (req, res) => {
   console.log('who get in here/users');
   res.json(users)
});



app.listen(80, () => {
  console.log('Example app listening on port 3000!');
});
