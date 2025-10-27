const Mock = require('mockjs')

// 用户数据
Mock.mock('/api/user/info', 'get', {
    code: 200,
    data: {
        id: '@id',
        nickname: '@cname',
        avatar: '@image(100x100)',
        stats: {
            boxCount: '@integer(0,10)',
            itemCount: '@integer(10,100)',
            categoryCount: '@integer(5,15)'
        }
    }
})

// 物品列表
Mock.mock(/\/api\/items(\?.*)?$/, 'get', {
    code: 200,
    'data|10-20': [{
        'id|+1': 1,
        'itemName': '@ctitle(3,10)',
        'description': '@cparagraph(1,3)',
        'image': '@image(200x200)',
        'boxName': '@ctitle(2,4)号收纳盒',
        'categoryId|1-5': 1,
        'createTime': '@datetime'
    }]
})

// 收纳盒列表
Mock.mock('/api/boxes', 'get', {
    code: 200,
    'data|3-8': [{
        'id|+1': 1,
        'boxCode': /BOX[A-Z0-9]{6}/,
        'boxName': '@ctitle(2,4)号收纳盒',
        'status|0-1': 1,
        'rssi|1': [-45, -55, -65, -75],
        'battery|20-100': 1,
        'lastHeartbeatTime': '@datetime'
    }]
})

// AI 对话
Mock.mock('/api/chat', 'post', {
    code: 200,
    data: {
        reply: '@cparagraph(1,3)',
        items: [{
            id: '@id',
            itemName: '@ctitle(3,10)',
            boxName: '@ctitle(2,4)号收纳盒'
        }]
    }
})
