import type { 
  RestResult, 
  PageResult,
  BoxDTO, 
  ItemDTO, 
  UserDTO, 
  AiChatResponse,
  EmotionDTO 
} from '@/common/types';

// ==========================================
// 盒子 Mock 数据
// ==========================================

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
      createTime: "2023-01-01 10:00:00",
      updateTime: "2023-12-10 14:30:00"
    }
  ]
};

export const mockBoxPage: RestResult<PageResult<BoxDTO>> = {
  code: 200,
  msg: "success",
  data: {
    page: 1,
    size: 10,
    total: 2,
    totalPages: 1,
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
        lastHeartbeatTime: "2023-12-09 09:15:00"
      }
    ]
  }
};

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
    lastHeartbeatTime: "2023-12-10 14:30:00"
  }
};

// ==========================================
// 物品 Mock 数据
// ==========================================

export const mockItemList: RestResult<ItemDTO[]> = {
  code: 200,
  msg: "success",
  data: [
    { 
      id: 101, 
      boxId: 1, 
      itemCode: "ITM_001", 
      autoRecognizeName: "Nintendo Switch",
      manualEditName: "Switch游戏机", 
      itemTag: "数码", 
      isValid: 1, 
      putInTime: "2023-01-15 10:00:00"
    },
    { 
      id: 102, 
      boxId: 1, 
      itemCode: "ITM_002", 
      autoRecognizeName: "Power Bank",
      manualEditName: "充电宝", 
      itemTag: "数码", 
      isValid: 1, 
      putInTime: "2023-02-20 14:00:00"
    }
  ]
};

export const mockItemPage: RestResult<PageResult<ItemDTO>> = {
  code: 200,
  msg: "success",
  data: {
    page: 1,
    size: 10,
    total: 1,
    totalPages: 1,
    data: [
        { 
            id: 101, 
            boxId: 1, 
            itemCode: "ITM_001", 
            autoRecognizeName: "Nintendo Switch",
            manualEditName: "Switch游戏机", 
            itemTag: "数码", 
            isValid: 1, 
            putInTime: "2023-01-15 10:00:00"
        }
    ]
  }
};

export const mockItemDetail: RestResult<ItemDTO> = {
  code: 200,
  msg: "success",
  data: { 
    id: 101, 
    boxId: 1, 
    itemCode: "ITM_001", 
    autoRecognizeName: "Nintendo Switch",
    manualEditName: "Switch游戏机", 
    itemTag: "数码", 
    itemDesc: "红蓝手柄 限定版", 
    isValid: 1, 
    putInTime: "2023-01-15 10:00:00"
  }
};

// ==========================================
// 用户 Mock 数据
// ==========================================

export const mockUser: RestResult<UserDTO> = {
  code: 200,
  msg: "success",
  data: { 
    id: 1001, 
    username: "Admin", 
    email: "admin@example.com",
    createdAt: "2023-01-01 10:00:00",
    updatedAt: "2023-01-02 10:00:00"
  }
};

export const mockLoginResult: RestResult<any> = {
  code: 200,
  msg: "success",
  data: {
      token: "abcdef-123456-token",
      user: {
        id: 1001, 
        username: "Admin",
        email: "admin@magicbox.com",
        createdAt: "2023-01-01 12:00:00",
        updatedAt: "2023-01-02 12:00:00"
      }
  }
};

export const mockUserList: RestResult<UserDTO[]> = {
  code: 200,
  msg: "success",
  data: [
    { 
      id: 1001, 
      username: "Admin", 
      email: "admin@example.com",
      createdAt: "2023-01-01 10:00:00"
    }
  ]
};

export const mockUserPage: RestResult<PageResult<UserDTO>> = {
  code: 200,
  msg: "success",
  data: {
    page: 1,
    size: 10,
    total: 1,
    totalPages: 1,
    data: [
      { 
        id: 1001, 
        username: "Admin", 
        email: "admin@example.com",
        createdAt: "2023-01-01 10:00:00"
      }
    ]
  }
};

// ==========================================
// 情感标签与通用 Mock
// ==========================================

export const mockEmotionPage: RestResult<PageResult<EmotionDTO>> = {
  code: 200,
  msg: "success",
  data: {
    page: 1,
    size: 10,
    total: 1,
    totalPages: 1,
    data: [
        { id: 1, itemId: 101, emotionTag: 1, emotionRemark: "这是生日礼物" }
    ]
  }
};

export const mockEmotionList: RestResult<EmotionDTO[]> = {
  code: 200,
  msg: "success",
  data: [
      { id: 1, itemId: 101, emotionTag: 1, emotionRemark: "这是生日礼物" },
      { id: 2, itemId: 101, emotionTag: 2, emotionRemark: "有点旧了" }
  ]
};

export const mockChat: RestResult<AiChatResponse> = {
  code: 200,
  msg: "success",
  data: { 
    reply: "我帮你查到了，这个物品在【客厅杂物箱】里，建议您查看盒子 BOX_001。", 
    action: "OPEN_BOX_001" 
  }
};

export const mockSuccess: RestResult<any> = { 
  code: 200, 
  msg: "操作成功", 
  data: null 
};