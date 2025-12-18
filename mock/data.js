// 模拟数据
export const mockData = {
  // 用户数据
  users: [
    { id: 1, userAccount: 'admin', userName: '管理员', phone: '13800138000', createTime: '2023-01-01 12:00:00', updateTime: '2023-01-01 12:00:00' },
    { id: 2, userAccount: 'user1', userName: '用户1', phone: '13800138001', createTime: '2023-01-02 12:00:00', updateTime: '2023-01-02 12:00:00' },
    { id: 3, userAccount: 'user2', userName: '用户2', phone: '13800138002', createTime: '2023-01-03 12:00:00', updateTime: '2023-01-03 12:00:00' }
  ],
  
  // 盒子数据
  boxes: [
    { id: 1, boxId: 'BOX001', status: 0, battery: 80, createTime: '2023-01-01 12:00:00', updateTime: '2023-01-01 12:00:00' },
    { id: 2, boxId: 'BOX002', status: 1, battery: 30, createTime: '2023-01-02 12:00:00', updateTime: '2023-01-02 12:00:00' },
    { id: 3, boxId: 'BOX003', status: 2, battery: 10, createTime: '2023-01-03 12:00:00', updateTime: '2023-01-03 12:00:00' }
  ],
  
  // 物品数据
  items: [
    { id: 1, name: '物品1', price: 100.50, boxId: 1, createTime: '2023-01-01 12:00:00', updateTime: '2023-01-01 12:00:00' },
    { id: 2, name: '物品2', price: 200.75, boxId: 2, createTime: '2023-01-02 12:00:00', updateTime: '2023-01-02 12:00:00' },
    { id: 3, name: '物品3', price: 300.00, boxId: 3, createTime: '2023-01-03 12:00:00', updateTime: '2023-01-03 12:00:00' }
  ],
  
  // 情感标签数据
  emotions: [
    { id: 1, name: '开心', status: 0, createTime: '2023-01-01 12:00:00', updateTime: '2023-01-01 12:00:00' },
    { id: 2, name: '难过', status: 1, createTime: '2023-01-02 12:00:00', updateTime: '2023-01-02 12:00:00' },
    { id: 3, name: '愤怒', status: 0, createTime: '2023-01-03 12:00:00', updateTime: '2023-01-03 12:00:00' }
  ],
  
  // 错误恢复数据
  errors: [
    { id: 1, boxId: 'BOX001', errorMsg: '网络连接失败', status: 0, createTime: '2023-01-01 12:00:00', updateTime: '2023-01-01 12:00:00' },
    { id: 2, boxId: 'BOX002', errorMsg: '电池电量低', status: 1, createTime: '2023-01-02 12:00:00', updateTime: '2023-01-02 12:00:00' },
    { id: 3, boxId: 'BOX003', errorMsg: '传感器故障', status: 0, createTime: '2023-01-03 12:00:00', updateTime: '2023-01-03 12:00:00' }
  ]
}