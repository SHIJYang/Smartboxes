// common/types.ts

// 基础响应结构 (修复: message -> msg)
export interface RestResult<T> {
  code: number;
  msg: string; 
  data: T;
}

// 登录请求参数 (新增)
export interface LoginRequest {
  userAccount: string;
  userPassword: string;
}

// 盒子查询参数 (新增)
export interface QueryBoxDTO {
  userId?: number;
  boxCode?: string;
  boxName?: string;
  boxType?: number;
  status?: number;
}

// 盒子 (BoxDTO) (修复: 补全字段)
export interface BoxDTO {
  id: number;
  boxCode: string;
  userId: number;
  boxName: string;
  boxType: number; // 0-子箱，1-主箱
  status: number;  // 0-离线 1-在线 2-低电 3-故障
  rssi?: number;   // 新增: 信号强度
  battery?: number;
  lastHeartbeatTime?: string; // 新增
  createTime?: string;
  updateTime?: string;
}

// 商品查询参数 (新增)
export interface QueryItemDTO {
  boxId?: number;
  itemCode?: string;
  itemTag?: string;
  isValid?: number;
}

// 商品 (ItemDTO) (修复: 补全字段)
export interface ItemDTO {
  id: number;
  boxId: number;   
  itemCode: string;
  itemName: string;
  itemDesc?: string;
  itemTag?: string; 
  price?: number;
  autoRecognizeName?: string; // 新增
  manualEditName?: string;    // 新增
  isValid: number; 
  putInTime?: string;         // 新增
  expireTime?: string;
  createTime?: string;
  updateTime?: string;
}

// 用户 (UserDTO) (修复: 字段匹配)
export interface UserDTO {
  id: number;
  username: string;
  email?: string;
  createdAt?: string; // 对应 openapi: createdAt
  updatedAt?: string; // 对应 openapi: updatedAt
}
export interface UserDO {
  id?: number;
  userAccount: string;
  userPassword?: string;
  username?: string;
  phone?: string;
  createTime?: string;
  updateTime?: string;
}
// 情感标签 (EmotionDTO)
export interface EmotionDTO {
  id: number;
  itemId: number;
  emotionTag: number; 
  emotionRemark?: string;
}

// AI 聊天请求
export interface AiChatRequestDTO {
  userId: number;
  message: string;
}

// AI 聊天响应数据
export interface AiChatResponse {
  reply: string;    
  action?: string;  
}