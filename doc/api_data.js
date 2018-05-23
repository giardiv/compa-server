define({ "api": [  {    "type": "post",    "url": "/login",    "title": "Get token from user / password",    "name": "Login",    "group": "Auth",    "parameter": {      "fields": {        "Parameter": [          {            "group": "Parameter",            "type": "String",            "optional": false,            "field": "login",            "description": "<p>User's email</p>"          },          {            "group": "Parameter",            "type": "String",            "optional": false,            "field": "password",            "description": "<p>Users's raw password</p>"          }        ]      }    },    "success": {      "fields": {        "Success 200": [          {            "group": "Success 200",            "type": "String",            "optional": false,            "field": "Token",            "description": "<p>Token is returned</p>"          }        ]      }    },    "version": "0.0.0",    "filename": "src/main/compa/controllers/AuthController.java",    "groupTitle": "Auth",    "error": {      "fields": {        "Error 4xx": [          {            "group": "Error 4xx",            "optional": false,            "field": "IncorrectCredentials",            "description": "<p>Incorrect login or password</p>"          }        ]      }    }  },  {    "type": "post",    "url": "/register",    "title": "Register a new user",    "name": "Register",    "group": "Auth",    "parameter": {      "fields": {        "Parameter": [          {            "group": "Parameter",            "type": "String",            "optional": false,            "field": "login",            "description": "<p>User's email</p>"          },          {            "group": "Parameter",            "type": "String",            "optional": false,            "field": "password",            "description": "<p>Users's raw password</p>"          }        ]      }    },    "success": {      "fields": {        "Success 200": [          {            "group": "Success 200",            "type": "String",            "optional": false,            "field": "Token",            "description": "<p>Token is returned</p>"          }        ]      }    },    "version": "0.0.0",    "filename": "src/main/compa/controllers/AuthController.java",    "groupTitle": "Auth",    "error": {      "fields": {        "Error 4xx": [          {            "group": "Error 4xx",            "optional": false,            "field": "UserAlreadyExist",            "description": "<p>1001 : The <code>login</code> is already used.</p>"          },          {            "group": "Error 4xx",            "optional": false,            "field": "PasswordTooShort",            "description": "<p>1002 : The password require at least {@value compa.daos.UserDAO#PASSWORD_MIN_LENGTH}</p>"          }        ]      }    }  },  {    "type": "post",    "url": "/updatePassword",    "title": "Update the password",    "name": "Update_Password",    "group": "Auth",    "parameter": {      "fields": {        "Parameter": [          {            "group": "Parameter",            "type": "String",            "optional": false,            "field": "password",            "description": "<p>Users's raw password</p>"          }        ]      }    },    "success": {      "fields": {        "Success 200": [          {            "group": "Success 200",            "type": "String",            "optional": false,            "field": "Token",            "description": "<p>A new token is returned</p>"          }        ]      }    },    "version": "0.0.0",    "filename": "src/main/compa/controllers/AuthController.java",    "groupTitle": "Auth",    "error": {      "fields": {        "Error 4xx": [          {            "group": "Error 4xx",            "optional": false,            "field": "PasswordTooShort",            "description": "<p>1002 : The password require at least {@value compa.daos.UserDAO#PASSWORD_MIN_LENGTH}</p>"          }        ]      }    }  },  {    "type": "get",    "url": "/friends",    "title": "Get the friends of the user",    "name": "GetFriendship",    "group": "Friendship",    "parameter": {      "fields": {        "Parameter": [          {            "group": "Parameter",            "type": "String",            "optional": false,            "field": "user_id",            "description": "<p>: id of user whose friend list is request</p>"          }        ]      }    },    "version": "0.0.0",    "filename": "src/main/compa/controllers/FriendshipController.java",    "groupTitle": "Friendship"  },  {    "type": "get",    "url": "/friendship/friendshipsDTO",    "title": "get friendship list",    "name": "GetFriendshipsDTOuser",    "group": "Friendship",    "parameter": {      "fields": {        "Parameter": [          {            "group": "Parameter",            "type": "String",            "optional": false,            "field": "user_id",            "description": "<p>: id of user whose friend list is request</p>"          }        ]      }    },    "version": "0.0.0",    "filename": "src/main/compa/controllers/FriendshipController.java",    "groupTitle": "Friendship"  },  {    "type": "post",    "url": "/friendship/request",    "title": "Add a new friendship",    "name": "Request_Friendship",    "group": "Friendship",    "parameter": {      "fields": {        "Parameter": [          {            "group": "Parameter",            "type": "String",            "optional": false,            "field": "friend_id",            "description": "<p>: the id of the user you want to become friends with</p>"          }        ]      }    },    "version": "0.0.0",    "filename": "src/main/compa/controllers/FriendshipController.java",    "groupTitle": "Friendship",    "error": {      "fields": {        "Error 4xx": [          {            "group": "Error 4xx",            "optional": false,            "field": "FriendshipAlreadyExist",            "description": "<p>The friendship is already defined.</p>"          }        ]      }    }  },  {    "type": "get",    "url": "/user",    "title": "Get current user profile data",    "name": "GetMe",    "group": "User",    "version": "0.0.0",    "filename": "src/main/compa/controllers/UserController.java",    "groupTitle": "User",    "success": {      "fields": {        "Success 200": [          {            "group": "Success 200",            "type": "String",            "optional": false,            "field": "id",            "description": "<p>The current User Id</p>"          },          {            "group": "Success 200",            "type": "String",            "optional": false,            "field": "login",            "description": "<p>User e-mail</p>"          },          {            "group": "Success 200",            "type": "String",            "optional": false,            "field": "name",            "description": "<p>Name</p>"          },          {            "group": "Success 200",            "type": "Boolean",            "optional": false,            "field": "ghostMode",            "description": "<p>If ghost more is enable</p>"          },          {            "group": "Success 200",            "type": "LocationDTO",            "optional": false,            "field": "lastLocation",            "description": "<p>The current User Id</p>"          }        ]      }    }  },  {    "type": "get",    "url": "/user/:id",    "title": "Get the profile of the user with :id",    "name": "GetMe",    "group": "User",    "version": "0.0.0",    "filename": "src/main/compa/controllers/UserController.java",    "groupTitle": "User",    "success": {      "fields": {        "Success 200": [          {            "group": "Success 200",            "type": "String",            "optional": false,            "field": "id",            "description": "<p>The current User Id</p>"          },          {            "group": "Success 200",            "type": "String",            "optional": false,            "field": "login",            "description": "<p>User e-mail</p>"          },          {            "group": "Success 200",            "type": "String",            "optional": false,            "field": "name",            "description": "<p>Name</p>"          },          {            "group": "Success 200",            "type": "Boolean",            "optional": false,            "field": "ghostMode",            "description": "<p>If ghost more is enable</p>"          },          {            "group": "Success 200",            "type": "LocationDTO",            "optional": false,            "field": "lastLocation",            "description": "<p>The current User Id</p>"          }        ]      }    }  },  {    "type": "put",    "url": "/user/ghostmode",    "title": "Set ghost mode",    "name": "GetMe",    "group": "User",    "parameter": {      "fields": {        "Parameter": [          {            "group": "Parameter",            "type": "Boolean",            "optional": false,            "field": "mode",            "description": "<p>If ghost mode is swith on/off</p>"          }        ]      }    },    "success": {      "fields": {        "Success 200": [          {            "group": "Success 200",            "optional": false,            "field": "Return",            "description": "<p>200 without body</p>"          }        ]      }    },    "version": "0.0.0",    "filename": "src/main/compa/controllers/UserController.java",    "groupTitle": "User"  },  {    "type": "get",    "url": "/location",    "title": "Get fake data location",    "version": "0.0.0",    "filename": "src/main/compa/controllers/FakeController.java",    "group": "_Users_Vincent_Lines_compa_compa_server_src_main_compa_controllers_FakeController_java",    "groupTitle": "_Users_Vincent_Lines_compa_compa_server_src_main_compa_controllers_FakeController_java",    "name": "GetLocation"  }] });
