import Mock from 'mockjs'
import { mockData } from './data'

// 模拟登录接口
Mock.mock('/api/users/login', 'post', (options) => {
  const { userAccount, userPassword } = JSON.parse(options.body)
  // 简单的验证逻辑
  if (userAccount === 'admin' && userPassword === '123456') {
    return {
      code: 0,
      data: {
        token: 'mock-token-' + Date.now()
      },
      message: '登录成功'
    }
  } else {
    return {
      code: 1,
      data: null,
      message: '用户名或密码错误'
    }
  }
})

// 模拟获取当前用户信息接口
Mock.mock('/api/users/me', 'get', () => {
  return {
    code: 0,
    data: {
      id: 1,
      userAccount: 'admin',
      userName: '管理员',
      phone: '13800138000'
    },
    message: '获取成功'
  }
})

// 模拟获取用户列表接口
Mock.mock('/api/users/page', 'get', (options) => {
  // 解析查询参数
  const urlParams = new URLSearchParams(options.url.split('?')[1])
  const keyword = urlParams.get('keyword') || ''
  const currentPage = parseInt(urlParams.get('currentPage')) || 1
  const pageSize = parseInt(urlParams.get('pageSize')) || 10
  
  // 根据关键字过滤数据
  let filteredUsers = mockData.users
  if (keyword) {
    filteredUsers = mockData.users.filter(user => 
      user.userName.includes(keyword) || user.userAccount.includes(keyword)
    )
  }
  
  // 分页处理
  const start = (currentPage - 1) * pageSize
  const end = start + pageSize
  const paginatedUsers = filteredUsers.slice(start, end)
  
  return {
    code: 0,
    data: paginatedUsers,
    message: '获取成功'
  }
})

// 模拟添加用户接口
Mock.mock('/api/users', 'post', (options) => {
  const newUser = JSON.parse(options.body)
  newUser.id = mockData.users.length + 1
  newUser.createTime = Mock.Random.datetime()
  newUser.updateTime = Mock.Random.datetime()
  mockData.users.push(newUser)
  
  return {
    code: 0,
    data: newUser,
    message: '添加成功'
  }
})

// 模拟更新用户接口
Mock.mock(/\/api\/users\/\d+/, 'put', (options) => {
  const userId = parseInt(options.url.match(/\/api\/users\/(\d+)/)[1])
  const updatedUser = JSON.parse(options.body)
  
  const index = mockData.users.findIndex(user => user.id === userId)
  if (index !== -1) {
    mockData.users[index] = {
      ...mockData.users[index],
      ...updatedUser,
      updateTime: Mock.Random.datetime()
    }
    
    return {
      code: 0,
      data: mockData.users[index],
      message: '更新成功'
    }
  } else {
    return {
      code: 1,
      data: null,
      message: '用户不存在'
    }
  }
})

// 模拟删除用户接口
Mock.mock(/\/api\/users\/\d+/, 'delete', (options) => {
  const userId = parseInt(options.url.match(/\/api\/users\/(\d+)/)[1])
  
  const index = mockData.users.findIndex(user => user.id === userId)
  if (index !== -1) {
    mockData.users.splice(index, 1)
    
    return {
      code: 0,
      data: null,
      message: '删除成功'
    }
  } else {
    return {
      code: 1,
      data: null,
      message: '用户不存在'
    }
  }
})

// 模拟获取盒子列表接口
Mock.mock('/api/boxes/page', 'get', (options) => {
  // 解析查询参数
  const urlParams = new URLSearchParams(options.url.split('?')[1])
  const keyword = urlParams.get('keyword') || ''
  const currentPage = parseInt(urlParams.get('currentPage')) || 1
  const pageSize = parseInt(urlParams.get('pageSize')) || 10
  
  // 根据关键字过滤数据
  let filteredBoxes = mockData.boxes
  if (keyword) {
    filteredBoxes = mockData.boxes.filter(box => 
      box.boxId.includes(keyword)
    )
  }
  
  // 分页处理
  const start = (currentPage - 1) * pageSize
  const end = start + pageSize
  const paginatedBoxes = filteredBoxes.slice(start, end)
  
  return {
    code: 0,
    data: paginatedBoxes,
    message: '获取成功'
  }
})

// 模拟添加盒子接口
Mock.mock('/api/boxes/add', 'post', (options) => {
  const newBox = JSON.parse(options.body)
  newBox.id = mockData.boxes.length + 1
  newBox.createTime = Mock.Random.datetime()
  newBox.updateTime = Mock.Random.datetime()
  mockData.boxes.push(newBox)
  
  return {
    code: 0,
    data: newBox,
    message: '添加成功'
  }
})

// 模拟更新盒子接口
Mock.mock('/api/boxes/update', 'post', (options) => {
  const updatedBox = JSON.parse(options.body)
  
  const index = mockData.boxes.findIndex(box => box.id === updatedBox.id)
  if (index !== -1) {
    mockData.boxes[index] = {
      ...mockData.boxes[index],
      ...updatedBox,
      updateTime: Mock.Random.datetime()
    }
    
    return {
      code: 0,
      data: mockData.boxes[index],
      message: '更新成功'
    }
  } else {
    return {
      code: 1,
      data: null,
      message: '盒子不存在'
    }
  }
})

// 模拟删除盒子接口
Mock.mock('/api/boxes/delete', 'post', (options) => {
  const { id } = JSON.parse(options.body)
  
  const index = mockData.boxes.findIndex(box => box.id === id)
  if (index !== -1) {
    mockData.boxes.splice(index, 1)
    
    return {
      code: 0,
      data: null,
      message: '删除成功'
    }
  } else {
    return {
      code: 1,
      data: null,
      message: '盒子不存在'
    }
  }
})

// 模拟获取物品列表接口
Mock.mock('/api/items/list', 'get', () => {
  return {
    code: 0,
    data: mockData.items,
    message: '获取成功'
  }
})

// 模拟添加物品接口
Mock.mock('/api/items/add', 'post', (options) => {
  const newItem = JSON.parse(options.body)
  newItem.id = mockData.items.length + 1
  newItem.createTime = Mock.Random.datetime()
  newItem.updateTime = Mock.Random.datetime()
  mockData.items.push(newItem)
  
  return {
    code: 0,
    data: newItem,
    message: '添加成功'
  }
})

// 模拟更新物品接口
Mock.mock('/api/items/update', 'post', (options) => {
  const updatedItem = JSON.parse(options.body)
  
  const index = mockData.items.findIndex(item => item.id === updatedItem.id)
  if (index !== -1) {
    mockData.items[index] = {
      ...mockData.items[index],
      ...updatedItem,
      updateTime: Mock.Random.datetime()
    }
    
    return {
      code: 0,
      data: mockData.items[index],
      message: '更新成功'
    }
  } else {
    return {
      code: 1,
      data: null,
      message: '物品不存在'
    }
  }
})

// 模拟删除物品接口
Mock.mock('/api/items/delete', 'post', (options) => {
  const { id } = JSON.parse(options.body)
  
  const index = mockData.items.findIndex(item => item.id === id)
  if (index !== -1) {
    mockData.items.splice(index, 1)
    
    return {
      code: 0,
      data: null,
      message: '删除成功'
    }
  } else {
    return {
      code: 1,
      data: null,
      message: '物品不存在'
    }
  }
})

// 模拟获取情感标签列表接口
Mock.mock('/api/emotions/page', 'get', (options) => {
  // 解析查询参数
  const urlParams = new URLSearchParams(options.url.split('?')[1])
  const keyword = urlParams.get('keyword') || ''
  const currentPage = parseInt(urlParams.get('currentPage')) || 1
  const pageSize = parseInt(urlParams.get('pageSize')) || 10
  
  // 根据关键字过滤数据
  let filteredEmotions = mockData.emotions
  if (keyword) {
    filteredEmotions = mockData.emotions.filter(emotion => 
      emotion.name.includes(keyword)
    )
  }
  
  // 分页处理
  const start = (currentPage - 1) * pageSize
  const end = start + pageSize
  const paginatedEmotions = filteredEmotions.slice(start, end)
  
  return {
    code: 0,
    data: paginatedEmotions,
    message: '获取成功'
  }
})

// 模拟添加情感标签接口
Mock.mock('/api/emotions/add', 'post', (options) => {
  const newEmotion = JSON.parse(options.body)
  newEmotion.id = mockData.emotions.length + 1
  newEmotion.createTime = Mock.Random.datetime()
  newEmotion.updateTime = Mock.Random.datetime()
  mockData.emotions.push(newEmotion)
  
  return {
    code: 0,
    data: newEmotion,
    message: '添加成功'
  }
})

// 模拟更新情感标签接口
Mock.mock('/api/emotions/update', 'post', (options) => {
  const updatedEmotion = JSON.parse(options.body)
  
  const index = mockData.emotions.findIndex(emotion => emotion.id === updatedEmotion.id)
  if (index !== -1) {
    mockData.emotions[index] = {
      ...mockData.emotions[index],
      ...updatedEmotion,
      updateTime: Mock.Random.datetime()
    }
    
    return {
      code: 0,
      data: mockData.emotions[index],
      message: '更新成功'
    }
  } else {
    return {
      code: 1,
      data: null,
      message: '情感标签不存在'
    }
  }
})

// 模拟删除情感标签接口
Mock.mock('/api/emotions/delete', 'post', (options) => {
  const { id } = JSON.parse(options.body)
  
  const index = mockData.emotions.findIndex(emotion => emotion.id === id)
  if (index !== -1) {
    mockData.emotions.splice(index, 1)
    
    return {
      code: 0,
      data: null,
      message: '删除成功'
    }
  } else {
    return {
      code: 1,
      data: null,
      message: '情感标签不存在'
    }
  }
})

// 模拟AI聊天接口
Mock.mock('/api/chat', 'post', (options) => {
  const { message } = JSON.parse(options.body)
  
  // 简单的AI回复逻辑
  let reply = '抱歉，我不太明白你的意思。'
  if (message.includes('你好')) {
    reply = '你好！有什么我可以帮助你的吗？'
  } else if (message.includes('谢谢')) {
    reply = '不客气！如果你有其他问题，随时问我。'
  } else if (message.includes('再见')) {
    reply = '再见！期待下次与你交流。'
  }
  
  return {
    code: 0,
    data: reply,
    message: '获取成功'
  }
})

// 模拟获取错误列表接口
Mock.mock('/api/errors/page', 'get', (options) => {
  // 解析查询参数
  const urlParams = new URLSearchParams(options.url.split('?')[1])
  const keyword = urlParams.get('keyword') || ''
  const currentPage = parseInt(urlParams.get('currentPage')) || 1
  const pageSize = parseInt(urlParams.get('pageSize')) || 10
  
  // 根据关键字过滤数据
  let filteredErrors = mockData.errors
  if (keyword) {
    filteredErrors = mockData.errors.filter(error => 
      error.boxId.includes(keyword) || error.errorMsg.includes(keyword)
    )
  }
  
  // 分页处理
  const start = (currentPage - 1) * pageSize
  const end = start + pageSize
  const paginatedErrors = filteredErrors.slice(start, end)
  
  return {
    code: 0,
    data: paginatedErrors,
    message: '获取成功'
  }
})

// 模拟触发错误恢复接口
Mock.mock('/api/errors/recover', 'post', (options) => {
  const { id } = JSON.parse(options.body)
  
  const index = mockData.errors.findIndex(error => error.id === id)
  if (index !== -1) {
    mockData.errors[index].status = 1
    mockData.errors[index].updateTime = Mock.Random.datetime()
    
    return {
      code: 0,
      data: null,
      message: '恢复指令已发送'
    }
  } else {
    return {
      code: 1,
      data: null,
      message: '错误记录不存在'
    }
  }
})

console.log('Mock API服务已启动')