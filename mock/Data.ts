import type { RestResult, BoxDTO, ItemDTO, UserDTO, AiChatResponse } from '@/common/types';

// 模拟盒子列表数据
export const mockBoxList: RestResult<BoxDTO[]> = {
  code: 200,
  msg: "success",
  data: [
    { 
      id: 1, 
      boxName: "客厅杂物箱", 
      boxCode: "BOX_001", 
      boxType: 1, 
      status: 1, 
      userId: 1001, 
      battery: 80,
      rssi: -55,
      lastHeartbeatTime: "2023-12-10 14:30:00",
      createTime: "2023-01-01 10:00:00",
      updateTime: "2023-12-10 14:30:00"
    },
    { 
      id: 2, 
      boxName: "卧室衣柜", 
      boxCode: "BOX_002", 
      boxType: 0, 
      status: 0, 
      userId: 1001, 
      battery: 30,
      rssi: -85,
      lastHeartbeatTime: "2023-12-09 09:15:00",
      createTime: "2023-02-15 11:20:00",
      updateTime: "2023-12-09 09:15:00"
    },
    { 
      id: 3, 
      boxName: "书房书架", 
      boxCode: "BOX_003", 
      boxType: 1, 
      status: 1, 
      userId: 1001, 
      battery: 90,
      rssi: -42,
      lastHeartbeatTime: "2023-12-10 15:00:00",
      createTime: "2023-03-10 14:00:00",
      updateTime: "2023-12-10 15:00:00"
    },
    { 
      id: 4, 
      boxName: "厨房储物柜", 
      boxCode: "BOX_004", 
      boxType: 0, 
      status: 1, 
      userId: 1001, 
      battery: 60,
      rssi: -60,
      lastHeartbeatTime: "2023-12-10 12:45:00",
      createTime: "2023-04-05 09:30:00",
      updateTime: "2023-12-10 12:45:00"
    }
  ]
};

// 模拟单个盒子详情
export const mockBoxDetail: RestResult<BoxDTO> = {
  code: 200,
  msg: "success",
  data: { 
    id: 1, 
    boxName: "客厅杂物箱", 
    boxCode: "BOX_001", 
    boxType: 1, 
    status: 1, 
    userId: 1001, 
    battery: 80,
    rssi: -55,
    lastHeartbeatTime: "2023-12-10 14:30:00",
    createTime: "2023-01-01 10:00:00",
    updateTime: "2023-12-10 14:30:00"
  }
};

// 模拟物品列表数据
export const mockItemList: RestResult<ItemDTO[]> = {
  code: 200,
  msg: "success",
  data: [
    { 
      id: 101, 
      boxId: 1, 
      itemCode: "ITM_001", 
      itemName: "Switch游戏机", 
      price: 2000, 
      itemTag: "数码", 
      isValid: 1, 
      putInTime: "2023-01-15 10:00:00",
      createTime: "2023-01-15 10:00:00",
      autoRecognizeName: "Nintendo Switch"
    },
    { 
      id: 102, 
      boxId: 1, 
      itemCode: "ITM_002", 
      itemName: "充电宝", 
      price: 100, 
      itemTag: "数码", 
      isValid: 1, 
      putInTime: "2023-02-20 14:00:00",
      createTime: "2023-02-20 14:00:00",
      autoRecognizeName: "Power Bank"
    },
    { 
      id: 103, 
      boxId: 2, 
      itemCode: "ITM_003", 
      itemName: "冬季外套", 
      price: 500, 
      itemTag: "衣物", 
      isValid: 1, 
      putInTime: "2023-01-10 09:00:00",
      createTime: "2023-01-10 09:00:00"
    },
    { 
      id: 104, 
      boxId: 2, 
      itemCode: "ITM_004", 
      itemName: "牛仔裤", 
      price: 200, 
      itemTag: "衣物", 
      isValid: 1, 
      putInTime: "2023-03-05 16:20:00",
      createTime: "2023-03-05 16:20:00"
    },
    { 
      id: 105, 
      boxId: 3, 
      itemCode: "ITM_005", 
      itemName: "JS高级程序设计", 
      price: 120, 
      itemTag: "书籍", 
      isValid: 1, 
      putInTime: "2023-02-15 11:00:00",
      createTime: "2023-02-15 11:00:00"
    },
    { 
      id: 106, 
      boxId: 3, 
      itemCode: "ITM_006", 
      itemName: "笔记本电脑", 
      price: 8000, 
      itemTag: "数码", 
      isValid: 1, 
      putInTime: "2023-01-20 13:00:00",
      createTime: "2023-01-20 13:00:00"
    },
    { 
      id: 107, 
      boxId: 4, 
      itemCode: "ITM_007", 
      itemName: "调料套装", 
      price: 80, 
      itemTag: "厨具", 
      isValid: 1, 
      putInTime: "2023-03-10 18:30:00",
      createTime: "2023-03-10 18:30:00"
    }
  ]
};

// 模拟单个物品详情
export const mockItemDetail: RestResult<ItemDTO> = {
  code: 200,
  msg: "success",
  data: { 
    id: 101, 
    boxId: 1, 
    itemCode: "ITM_001", 
    itemName: "Switch游戏机", 
    price: 2000, 
    itemTag: "数码", 
    itemDesc: "红蓝手柄", 
    isValid: 1, 
    putInTime: "2023-01-15 10:00:00",
    createTime: "2023-01-15 10:00:00",
    autoRecognizeName: "Nintendo Switch"
  }
};

// 模拟用户信息
// 注意：openapi中 UserDTO 不包含 avatar，但为了UI展示通常会保留在Mock数据中
export const mockUser: RestResult<UserDTO> = {
  code: 200,
  msg: "success",
  data: { 
    id: 1001, 
    username: "Admin", 
    email: "admin@test.com",
    createdAt: "2023-01-01 00:00:00",
    updatedAt: "2023-12-01 00:00:00"
  }
};

// 模拟聊天响应
export const mockChat: RestResult<AiChatResponse> = {
  code: 200,
  msg: "success",
  data: { 
    reply: "我帮你查到了，这个物品在【客厅杂物箱】里，建议您查看盒子 BOX_001。", 
    action: "OPEN_BOX_001" 
  }
};

// 模拟通用成功响应
export const mockSuccess: RestResult<any> = { 
  code: 200, 
  msg: "操作成功", 
  data: null 
};