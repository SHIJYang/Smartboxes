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
  boxType: number; // 0-子盒，1-主盒
  status: number;  // 0-离线 1-在线 2-低电 3-故障
  rssi?: number;   // 信号强度
  battery?: number; // 电量
  networkDelay?: number; // 网络延迟（仅主盒记录）
  lastHeartbeatTime?: string;
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

// 商品 (ItemDTO) - 根据SQL表和代码清理后的结构
export interface ItemDTO {
  id?: number;
  boxId: number;   
  itemCode: string;
  autoRecognizeName?: string;
  manualEditName?: string;
  itemTag?: string; 
  itemDesc?: string;
  putInTime?: string;
  expireTime?: string;
  isValid?: number; // 0-已取出，1-在盒内
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

// 情感标签查询参数
export interface QueryEmotionDTO {
  itemId?: number;
}

// 情感标签 (EmotionDTO)
export interface EmotionDTO {
  id?: number;
  itemId: number;
  emotionTag: number; // 1-开心，2-怀念，3-重要，4-其他
  emotionRemark?: string;
  createTime?: string;
  updateTime?: string;
}

// 错误日志 (ErrorLogDTO)
export interface ErrorLogDTO {
  errorCode: string;
  errorMessage: string;
  errorTime: string;
}

// 降级策略请求 (FallbackRequestDTO)
export interface FallbackRequestDTO {
  serviceId: string;
  fallbackReason: string;
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

// 用户查询参数 (UserQuery)
export interface UserQuery {
  keyword?: string;
  pageSize?: number;
  currentPage?: number;
}