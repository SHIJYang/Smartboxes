// common/types.ts

// 基础响应结构
export interface RestResult<T> {
  code: number;
  msg: string; 
  data: T;
}

// 分页数据结构
export interface PageResult<T> {
  page: number;
  size: number;
  total: number;
  totalPages?: number; // OpenAPI Schema 定义中未明确包含但通常会有，前端计算也行
  data: T[];
}

// 登录请求参数
export interface LoginRequest {
  userAccount: string;
  userPassword?: string; 
}

// 用户 (UserDTO) - 聚合 OpenAPI 定义与前端需求
export interface UserDTO {
  id: number;
  username: string;
  email?: string;        // OpenAPI 定义字段
  createdAt?: string;    // OpenAPI 定义字段
  updatedAt?: string;    // OpenAPI 定义字段
  
  // --- 前端兼容扩展字段 ---
  userAccount?: string;  // UserDO 中有，DTO 中可能不返回，前端需注意判空
  phone?: string;
  token?: string;        // 登录接口返回时附加
  avatar?: string;       // 前端UI展示用
}

// 用户实体 (UserDO) - 用于注册/更新
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

// 物品 (ItemDTO)
export interface ItemDTO {
  id?: number;
  itemCode: string;
  boxId: number;   
  autoRecognizeName?: string; 
  manualEditName?: string;    
  itemTag?: string; 
  itemDesc?: string;
  putInTime?: string;
  expireTime?: string;
  isValid?: number; // 0-无效/已取出，1-有效/在盒内
}

// 情感标签 (EmotionDTO)
export interface EmotionDTO {
  id?: number;
  itemId: number;
  emotionTag: number; // 1-快乐 2-悲伤 3-愤怒 4-焦虑 5-平静
  emotionRemark?: string;
}

// 错误日志
export interface ErrorLogDTO {
  errorCode: string;
  errorMessage: string;
  errorTime: string;
}

// 降级策略请求
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

// --- 查询参数 ---

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

export interface QueryItemDTO {
  page?: number;
  size?: number;
  userId?: number;
  boxId?: number;
  itemCode?: string;
  name?: string; 
  itemTag?: string;
  isValid?: number;
}

export interface QueryUserDTO {
  currentPage?: number;
  pageSize?: number;
  keyword?: string;
}