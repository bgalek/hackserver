import { Record } from 'immutable'
const UserRecord = Record({
  name: '',
  isLoggedIn: false
})

export default class UserModel extends UserRecord {
  constructor (userObject) {
    userObject = Object.assign({}, userObject)
    userObject.isLoggedIn = !userObject.anonymous || userObject.root
    super(userObject)
  }
};
