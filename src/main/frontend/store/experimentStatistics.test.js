import apiError from './apiError'

test('handle Andamio error', () => {
  var error = { response: { data: { errors: [{message:'some druid error'}] } } }
  expect(apiError(error)).toEqual(Error('some druid error'))
})

test('ignore errors without message', () => {
  var error = { response: { data: { errors: [{msg:'fail'}] } } }
  expect(apiError(error)).toBe(error)
})

test('ignore empty errors', () => {
  var error = { response: { data: { errors: [] } } }
  expect(apiError(error)).toBe(error)
})
