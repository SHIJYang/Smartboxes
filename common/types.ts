// common/types.ts

// 基础响应结构
export interface RestResult<T> {
  code: number;
  msg: string; 
  data: T;
}

// 分页数据结构 (新增，对应 RestResult_PageResult_*)
export interface PageResult<T> {
  page: number;
  size: number;
  total: number;
  totalPages: number;
  data: T[];
}

// 登录请求参数
export interface LoginRequest {
  userAccount: string;
  userPassword?: string; // OpenAPI中未强制，但在请求体中通常必填，这里设为可选兼容UserDO
}

// 用户 (UserDTO) - 对应 openapi schemas/UserDTO
export interface UserDTO {
  id: number;
  userAccount: string;
  username: string;
  phone?: string;
  token?: string; // 登录成功后返回
  // email?: string; // OpenAPI 中无此字段，已移除
}

// 用户实体 (UserDO) - 用于注册
export interface UserDO {
  id?: number;
  userAccount: string;
  userPassword?: string;
  username?: string;
  phone?: string;
}

// 盒子 (BoxDTO)
export interface BoxDTO {
  id?: number;
  boxCode: string;
  userId: number;
  boxName: string;
  boxType: number; // 0-子盒，1-主盒
  status: number;  // 0-离线 1-在线 2-低电 3-故障
  rssi?: number;   
  battery?: number; 
  networkDelay?: number;
  lastHeartbeatTime?: string;
  createTime?: string;
  updateTime?: string;
}

// 物品 (ItemDTO) - 修正字段以匹配 OpenAPI
export interface ItemDTO {
  id?: number;
  boxId: number;   
  itemCode: string;
  autoRecognizeName?: string; // 自动识别名称
  manualEditName?: string;    // 手动编辑名称
  itemTag?: string; 
  itemDesc?: string;
  putInTime?: string;
  expireTime?: string;
  isValid?: number; // 0-已取出，1-在盒内
  // price?: number; // OpenAPI 中无此字段，已移除
  // itemName?: string; // OpenAPI 中无此字段，需使用 autoRecognizeName 或 manualEditName
}

// 情感标签 (EmotionDTO)
export interface EmotionDTO {
  id?: number;
  itemId: number;
  emotionTag: number; // 1-快乐 2-悲伤 3-愤怒 4-焦虑 5-平静
  emotionRemark?: string;
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

// AI 聊天响应
export interface AiChatResponse {
  reply: string;    
  action?: string;  
}

// --- 查询参数接口 ---

// 盒子查询参数
export interface QueryBoxDTO {
  page?: number;
  size?: number;
  sortField?: string;
  sortOrder?: string;
  userId?: number;
  boxCode?: string;
  boxName?: string;
  boxType?: number;
  status?: number;
}

// 物品查询参数
export interface QueryItemDTO {
  page?: number;
  size?: number;
  userId?: number;
  boxId?: number;
  itemCode?: string;
  name?: string; // 匹配自动名称或手动名称
  itemTag?: string;
  isValid?: number;
}

// 情感标签查询参数
export interface QueryEmotionDTO {
  page?: number;
  size?: number;
  itemId?: number;
}