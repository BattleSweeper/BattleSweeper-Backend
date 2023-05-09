<h2 align="center">BattleSweeper-Backend</h2>
### API v1 Specifications
+ `ws:/queue/register`: Register a user to the game-matching queue
  + params:
    + user: object
      + name: string
      + id: uuid
    + flags?: int
</br></br>
+ `ws:/room/connect`: Connect user to a room, after matching is completed
  + params:
    + room_id: uuid
    + user: object
      + name: string
      + id: uuid
</br></br>
+ `ws:/room/`: 