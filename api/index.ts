import { request } from '@/utils/request';
import type { 
  BoxDTO, 
  ItemDTO, 
  UserDTO, 
  UserDO,
  AiChatResponse, 
  AiChatRequestDTO, 
  LoginRequest,
  LoginResult,
  QueryBoxDTO,
  QueryItemDTO,
  QueryEmotionDTO,
  QueryUserDTO,
  EmotionDTO,
  ErrorLogDTO,
  FallbackRequestDTO,
  PageResult
} from '@/common/types';
import * as M from '@/mock/data';

// ==========================================
// 盒子管理相关接口 (Box)
// ==========================================

// 分页获取盒子列表
export const getBoxPage = (params: QueryBoxDTO) => 
  request<PageResult<BoxDTO>>('/api/boxes/page', 'GET', params, M.mockBoxPage);

// 获取盒子列表 (无分页)
export const getBoxList = (params: QueryBoxDTO) => 
  request<BoxDTO[]>('/api/boxes/list', 'GET', params, M.mockBoxList);

// 新增盒子
export const addBox = (data: BoxDTO) => 
  request<void>('/api/boxes/add', 'POST', data, M.mockSuccess);

// 更新盒子信息
export const updateBox = (data: BoxDTO) => 
  request<void>('/api/boxes/update', 'PUT', data, M.mockSuccess);

// 上报盒子状态
export const reportBoxStatus = (data: BoxDTO) => 
  request<void>('/api/boxes/report/status', 'PUT', data, M.mockSuccess);

// 删除盒子 (OpenAPI存在的接口)
export const deleteBox = (id: number) => 
  request<void>(`/api/boxes/delete/${id}`, 'DELETE', {}, M.mockSuccess);

// 注意：OpenAPI 确实没有 getBoxDetail (GET /api/boxes/{id})。
// 前端应从 getBoxPage/List 中查找。
// 这里保留 Mock 用于兼容，但实际后端可能报 404。
export const getBoxDetailMock = (id: number) => 
  request<BoxDTO>(`/api/boxes/${id}`, 'GET', {}, M.mockBoxDetail); 

// ==========================================
// 物品管理相关接口 (Item)
// ==========================================

// 获取物品列表 (无分页)
export const getItemList = (params: QueryItemDTO) => 
  request<ItemDTO[]>('/api/items/list', 'GET', params, M.mockItemList);

// 根据盒子ID获取物品
export const getItemsByBox = (boxId: number) => 
  request<ItemDTO[]>(`/api/items/by-box/${boxId}`, 'GET', {}, M.mockItemList);

// 根据用户ID获取物品
export const getItemsByUser = (userId: number) => 
  request<ItemDTO[]>(`/api/items/by-user/${userId}`, 'GET', {}, M.mockItemList);

// 分页获取物品列表
export const getItemPage = (params: QueryItemDTO) => 
  request<PageResult<ItemDTO>>('/api/items/page', 'GET', params, M.mockItemPage);

// 新增物品
export const addItem = (data: ItemDTO) => 
  request<void>('/api/items/add', 'POST', data, M.mockSuccess);

// 更新物品
export const updateItem = (data: ItemDTO) => 
  request<void>('/api/items/update', 'PUT', data, M.mockSuccess);

// 移动物品到另一个盒子
export const moveItem = (itemId: number, boxId: number) => 
  request<void>(`/api/items/move/${itemId}/to/${boxId}`, 'PUT', {}, M.mockSuccess);

// 更新物品状态 (OpenAPI: /api/items/status/{id}/{isValid})
export const updateItemStatus = (id: number, isValid: number) => 
  request<void>(`/api/items/status/${id}/${isValid}`, 'PUT', {}, M.mockSuccess);

// 删除物品 (OpenAPI存在的接口)
export const deleteItem = (id: number) => 
  request<void>(`/api/items/delete/${id}`, 'DELETE', {}, M.mockSuccess);

// 获取物品详情
export const getItemDetail = (id: number) => 
  request<ItemDTO>(`/api/items/detail/${id}`, 'GET', {}, M.mockItemDetail);

// ==========================================
// 情感标签管理 (Emotion)
// ==========================================

// 添加情感标签
export const addEmotion = (data: EmotionDTO) => 
  request<void>('/api/emotions/add', 'POST', data, M.mockSuccess);

// 更新情感标签
export const updateEmotion = (data: EmotionDTO) => 
  request<void>('/api/emotions/update', 'PUT', data, M.mockSuccess);

// 删除情感标签
export const deleteEmotion = (id: number) => 
  request<void>(`/api/emotions/delete/${id}`, 'DELETE', {}, M.mockSuccess);

// 获取情感标签列表 (无分页)
export const getEmotionList = (itemId: number) => 
  request<EmotionDTO[]>('/api/emotions/list', 'GET', { itemId }, M.mockEmotionList);

// 分页查询情感标签
export const getEmotionPage = (params: QueryEmotionDTO) => 
  request<PageResult<EmotionDTO>>('/api/emotions/page', 'GET', params, M.mockEmotionPage);

// ==========================================
// 用户与AI相关接口 (User & Chat)
// ==========================================

// 用户登录
export const login = (data: LoginRequest) => 
  request<any>('/api/users/login', 'POST', data, M.mockLoginResult);

// 用户注册 (返回 UserDTO)
export const registerUser = (data: UserDO) => 
  request<UserDTO>('/api/users', 'POST', data, M.mockUser);

// 获取所有用户
export const getUserList = () => 
  request<UserDTO[]>('/api/users', 'GET', {}, M.mockUserList);

// 分页查询用户
export const getUserPage = (params: QueryUserDTO) => 
  request<PageResult<UserDTO>>('/api/users/page', 'GET', params, M.mockUserPage);

// 根据ID获取用户
export const getUserDetail = (id: number) => 
  request<UserDTO>(`/api/users/${id}`, 'GET', {}, M.mockUser);

// 更新用户
export const updateUser = (id: number, data: UserDO) => 
  request<UserDTO>(`/api/users/${id}`, 'PUT', data, M.mockUser);

// 删除用户
export const deleteUser = (id: number) => 
  request<void>(`/api/users/${id}`, 'DELETE', {}, M.mockSuccess);

// AI 对话
export const sendChat = (data: AiChatRequestDTO) => 
  request<AiChatResponse>('/api/chat', 'POST', data, M.mockChat);

// ==========================================
// 错误修复管理 (Error Recovery)
// ==========================================

// 记录运行时错误
export const recordError = (data: ErrorLogDTO) => 
  request<void>('/api/error-recovery/record-error', 'POST', data, M.mockSuccess);

// 触发优雅降级
export const triggerFallback = (data: FallbackRequestDTO) => 
  request<any>('/api/error-recovery/trigger-fallback', 'POST', data, M.mockSuccess);