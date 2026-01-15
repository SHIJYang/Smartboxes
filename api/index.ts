import { request } from '@/utils/request';
import type { 
  BoxDTO, 
  ItemDTO, 
  UserDTO, 
  UserDO,
  AiChatResponse, 
  AiChatRequestDTO, 
  LoginRequest,
  QueryBoxDTO,
  QueryItemDTO,
  QueryEmotionDTO,
  QueryUserDTO, // 注意：OpenAPI中对应 schemas/UserQuery
  EmotionDTO,
  ErrorLogDTO,
  FallbackRequestDTO,
  PageResult,
  PageQueryDTO
} from '@/common/types';
import * as M from '@/mock/data';

// ==========================================
// 1. 盒子管理 (Box Management)
// ==========================================

/**
 * 分页获取盒子列表
 * OpenAPI: GET /api/boxes/page
 * Parameters: queryBoxDTO, pageQueryDTO
 */
export const getBoxPage = (params: QueryBoxDTO & PageQueryDTO) => 
  request<PageResult<BoxDTO>>('/api/boxes/page', 'GET', params, M.mockBoxPage);

/**
 * 获取盒子列表 (无分页)
 * OpenAPI: GET /api/boxes/list
 * Parameters: queryBoxDTO
 */
export const getBoxList = (params: QueryBoxDTO) => 
  request<BoxDTO[]>('/api/boxes/list', 'GET', params, M.mockBoxList);

/**
 * 新增盒子
 * OpenAPI: POST /api/boxes/add
 * Body: BoxDTO
 */
export const addBox = (data: BoxDTO) => 
  request<void>('/api/boxes/add', 'POST', data, M.mockSuccess);

/**
 * 修改盒子信息
 * OpenAPI: PUT /api/boxes/update
 * Body: BoxDTO
 */
export const updateBox = (data: BoxDTO) => 
  request<void>('/api/boxes/update', 'PUT', data, M.mockSuccess);

/**
 * 上报盒子状态
 * OpenAPI: PUT /api/boxes/report/status
 * Body: BoxDTO
 */
export const reportBoxStatus = (data: BoxDTO) => 
  request<void>('/api/boxes/report/status', 'PUT', data, M.mockSuccess);

/**
 * 删除盒子
 * OpenAPI: DELETE /api/boxes/delete/{id}
 */
export const deleteBox = (id: number) => 
  request<void>(`/api/boxes/delete/${id}`, 'DELETE', {}, M.mockSuccess);

// [注] OpenAPI 未定义 GET /api/boxes/{id}，保留此 Mock 仅作兼容，实际后端可能会 404
export const getBoxDetailMock = (id: number) => 
  request<BoxDTO>(`/api/boxes/${id}`, 'GET', {}, M.mockBoxDetail); 


// ==========================================
// 2. 物品管理 (Item Management)
// ==========================================

/**
 * 综合分页查询物品
 * OpenAPI: GET /api/items/page
 * Parameters: queryItemDTO, pageQueryDTO
 */
export const getItemPage = (params: QueryItemDTO & PageQueryDTO) => 
  request<PageResult<ItemDTO>>('/api/items/page', 'GET', params, M.mockItemPage);

/**
 * 查询物品列表 (无分页)
 * OpenAPI: GET /api/items/list
 * Parameters: queryItemDTO
 */
export const getItemList = (params: QueryItemDTO) => 
  request<ItemDTO[]>('/api/items/list', 'GET', params, M.mockItemList);

/**
 * 根据盒子找物品
 * OpenAPI: GET /api/items/by-box/{boxId}
 */
export const getItemsByBox = (boxId: number) => 
  request<ItemDTO[]>(`/api/items/by-box/${boxId}`, 'GET', {}, M.mockItemList);

/**
 * 根据用户找物品
 * OpenAPI: GET /api/items/by-user/{userId}
 */
export const getItemsByUser = (userId: number) => 
  request<ItemDTO[]>(`/api/items/by-user/${userId}`, 'GET', {}, M.mockItemList);

/**
 * 查询物品详细信息
 * OpenAPI: GET /api/items/detail/{id}
 */
export const getItemDetail = (id: number) => 
  request<ItemDTO>(`/api/items/detail/${id}`, 'GET', {}, M.mockItemDetail);

/**
 * 添加新物品
 * OpenAPI: POST /api/items/add
 * Body: ItemDTO
 */
export const addItem = (data: ItemDTO) => 
  request<void>('/api/items/add', 'POST', data, M.mockSuccess);

/**
 * 更新物品信息
 * OpenAPI: PUT /api/items/update
 * Body: ItemDTO
 */
export const updateItem = (data: ItemDTO) => 
  request<void>('/api/items/update', 'PUT', data, M.mockSuccess);

/**
 * 移动物品
 * OpenAPI: PUT /api/items/move/{itemId}/to/{boxId}
 */
export const moveItem = (itemId: number, boxId: number) => 
  request<void>(`/api/items/move/${itemId}/to/${boxId}`, 'PUT', {}, M.mockSuccess);

/**
 * 更新物品状态
 * OpenAPI: PUT /api/items/status/{id}/{isValid}
 */
export const updateItemStatus = (id: number, isValid: number) => 
  request<void>(`/api/items/status/${id}/${isValid}`, 'PUT', {}, M.mockSuccess);

/**
 * 删除物品
 * OpenAPI: DELETE /api/items/delete/{id}
 */
export const deleteItem = (id: number) => 
  request<void>(`/api/items/delete/${id}`, 'DELETE', {}, M.mockSuccess);


// ==========================================
// 3. 情感标签管理 (Emotion Management)
// ==========================================

/**
 * 分页查询情感标签
 * OpenAPI: GET /api/emotions/page
 * Parameters: queryEmotionDTO, pageQueryDTO
 */
export const getEmotionPage = (params: QueryEmotionDTO & PageQueryDTO) => 
  request<PageResult<EmotionDTO>>('/api/emotions/page', 'GET', params, M.mockEmotionPage);

/**
 * 查询情感标签列表(无分页)
 * OpenAPI: GET /api/emotions/list
 * Parameters: queryEmotionDTO
 */
export const getEmotionList = (params: QueryEmotionDTO) => 
  request<EmotionDTO[]>('/api/emotions/list', 'GET', params, M.mockEmotionList);

/**
 * 添加情感标签
 * OpenAPI: POST /api/emotions/add
 * Body: EmotionDTO
 */
export const addEmotion = (data: EmotionDTO) => 
  request<void>('/api/emotions/add', 'POST', data, M.mockSuccess);

/**
 * 更新情感标签
 * OpenAPI: PUT /api/emotions/update
 * Body: EmotionDTO
 */
export const updateEmotion = (data: EmotionDTO) => 
  request<void>('/api/emotions/update', 'PUT', data, M.mockSuccess);

/**
 * 删除情感标签
 * OpenAPI: DELETE /api/emotions/delete/{id}
 */
export const deleteEmotion = (id: number) => 
  request<void>(`/api/emotions/delete/${id}`, 'DELETE', {}, M.mockSuccess);


// ==========================================
// 4. 用户管理 (User Management)
// ==========================================

/**
 * 用户登录
 * OpenAPI: POST /api/users/login
 * Body: LoginRequest
 */
export const login = (data: LoginRequest) => 
  request<any>('/api/users/login', 'POST', data, M.mockLoginResult);

/**
 * 创建用户 (注册)
 * OpenAPI: POST /api/users
 * Body: UserDO
 */
export const registerUser = (data: UserDO) => 
  request<UserDTO>('/api/users', 'POST', data, M.mockUser);

/**
 * 获取所有用户
 * OpenAPI: GET /api/users
 */
export const getUserList = () => 
  request<UserDTO[]>('/api/users', 'GET', {}, M.mockUserList);

/**
 * 分页查询用户
 * OpenAPI: GET /api/users/page
 * Parameters: query (UserQuery)
 */
export const getUserPage = (params: QueryUserDTO) => 
  request<PageResult<UserDTO>>('/api/users/page', 'GET', params, M.mockUserPage);

/**
 * 根据ID获取用户
 * OpenAPI: GET /api/users/{id}
 */
export const getUserDetail = (id: number) => 
  request<UserDTO>(`/api/users/${id}`, 'GET', {}, M.mockUser);

/**
 * 更新用户
 * OpenAPI: PUT /api/users/{id}
 * Body: UserDO
 */
export const updateUser = (id: number, data: UserDO) => 
  request<UserDTO>(`/api/users/${id}`, 'PUT', data, M.mockUser);

/**
 * 删除用户
 * OpenAPI: DELETE /api/users/{id}
 */
export const deleteUser = (id: number) => 
  request<void>(`/api/users/${id}`, 'DELETE', {}, M.mockSuccess);


// ==========================================
// 5. AI 聊天 (AI Chat)
// ==========================================

/**
 * AI聊天
 * OpenAPI: POST /api/chat
 * Body: AiChatRequestDTO
 */
export const sendChat = (data: AiChatRequestDTO) => 
  request<AiChatResponse>('/api/chat', 'POST', data, M.mockChat);


// ==========================================
// 6. 错误修复管理 (Error Recovery)
// ==========================================

/**
 * 记录运行时错误
 * OpenAPI: POST /api/error-recovery/record-error
 * Body: ErrorLogDTO
 */
export const recordError = (data: ErrorLogDTO) => 
  request<void>('/api/error-recovery/record-error', 'POST', data, M.mockSuccess);

/**
 * 触发优雅降级策略
 * OpenAPI: POST /api/error-recovery/trigger-fallback
 * Body: FallbackRequestDTO
 */
export const triggerFallback = (data: FallbackRequestDTO) => 
  request<any>('/api/error-recovery/trigger-fallback', 'POST', data, M.mockSuccess);