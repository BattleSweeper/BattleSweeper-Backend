<h2 align="center">BattleSweeper-Backend</h2>

### API v1 Specifications
#### WebSocket
+ `ws|wss:/queue/register`: Register a user to the game-matching queue
  + params:
    + user: object
      + name: string
      + id: uuid
    + flags?: int
</br></br>
+ `ws|wss:/room`: Connect user to a room, after matching is completed
  + params:
    + room_id: uuid
    + user: object
      + name: string
      + id: uuid
</br></br>
#### REST
##### Registration
+ `POST http|https:/register`: Request email confirm before registration
  + params:
    + email: string
</br></br>
+ `POST http|https:/register/verify`: Verify email confirm code and register account
  + params:
    + email: string
    + code: string
    + password: string
    + name: string
</br></br>
##### Authentication
+ `POST http|https:/auth`: Generate JWT Token with id
  + params:
    + type: int // 1: Anonymous | 2: Registered
    + info: object
      + username: string // Only for anonymous login
      + email: string
      + password: string
</br></br>
+ `POST http|https:/auth/refresh`: Refresh JWT Token
  + params:
    + token: string // Refresh token from `/auth`