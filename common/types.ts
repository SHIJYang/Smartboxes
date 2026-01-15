// common/types.ts

// ==========================================
// 核心响应结构 (RestResult)
// ==========================================
export interface RestResult<T> {
  code: number;
  msg: string;
  data: T;
}

// ==========================================
// 分页响应结构 (PageResult)
// ==========================================
export interface PageResult<T> {
  page: number;
  size: number;
  total: number;
  totalPages: number;
  data: T[];
}

// ==========================================
// 认证与用户相关 (Auth & User)
// ==========================================

// POST /api/users/login
export interface LoginRequest {
  userAccount: string;
  userPassword?: string;
}

// Schema: UserDTO (用于查询返回)
export interface UserDTO {
  id: number;
  username: string;
  email?: string;
  createdAt?: string;
  updatedAt?: string;
  // --- 前端扩展字段 (OpenAPI Schema中未定义但部分接口可能返回，或用于UI) ---
  token?: string;
  avatar?: string;
}

// Schema: UserDO (用于注册/更新)
export interface UserDO {
  id?: number;
  userAccount?: string;
  userPassword?: string;
  username?: string;
  phone?: string;
  createTime?: string;
  updateTime?: string;
}

// ==========================================
// 盒子相关 (Box)
// ==========================================

// Schema: BoxDTO
export interface BoxDTO {
  id?: number;
  boxCode: string;
  userId: number;
  boxName: string;
  boxType: number; // 0-子盒, 1-主盒
  status: number;  // 0-3 状态码
  rssi?: number;   // -100 ~ 0
  battery?: number; // 0 ~ 100
  networkDelay?: number;
  lastHeartbeatTime?: string;
  createTime?: string;
  updateTime?: string;
}

// Schema: QueryBoxDTO (查询参数)
export interface QueryBoxDTO {
  userId?: number;
  boxCode?: string;
  boxName?: string;
  boxType?: number;
  status?: number;
}

// ==========================================
// 物品相关 (Item)
// ==========================================

// Schema: ItemDTO
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
  isValid?: number; // 0 or 1
}

// Schema: QueryItemDTO (查询参数)
export interface QueryItemDTO {
  userId?: number;
  boxId?: number;
  itemCode?: string;
  name?: string;
  itemTag?: string;
  isValid?: number;
}

// ==========================================
// 情感标签相关 (Emotion)
// ==========================================

// Schema: EmotionDTO
export interface EmotionDTO {
  id?: number;
  itemId: number;
  emotionTag: number;
  emotionRemark?: string;
}

// Schema: QueryEmotionDTO (查询参数)
export interface QueryEmotionDTO {
  itemId?: number;
}

// ==========================================
// 公共查询参数 (Query Params)
// ==========================================

// Schema: PageQueryDTO
// 用于: 物品(Items), 盒子(Boxes), 情感(Emotions)
export interface PageQueryDTO {
  page?: number;
  size?: number;
  sortField?: string;
  sortOrder?: string;
}

// Schema: UserQuery
// 特别注意：OpenAPI中用户管理的分页参数命名不同
export interface UserQuery extends QueryUserDTO {
  // 继承空接口或直接定义，保持命名与 OpenAPI UserQuery 一致
}

// 为了代码兼容性，定义具体字段
export interface QueryUserDTO {
  currentPage?: number; // 注意：不是 page
  pageSize?: number;    // 注意：不是 size
  keyword?: string;
}

// ==========================================
// 其他业务实体
// ==========================================

// Schema: ErrorLogDTO
export interface ErrorLogDTO {
  errorCode: string;
  errorMessage: string;
  errorTime: string;
}

// Schema: FallbackRequestDTO
export interface FallbackRequestDTO {
  serviceId: string;
  fallbackReason: string;
}

// Schema: AiChatRequestDTO
export interface AiChatRequestDTO {
  userId: number;
  message: string;
}

// Schema: AiChatResponse (OpenAPI定义在Response body中)
export interface AiChatResponse {
  reply: string;
  action?: string;
}