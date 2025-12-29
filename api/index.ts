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
  EmotionDTO,
  ErrorLogDTO,
  FallbackRequestDTO,
  PageResult
} from '@/common/types';
import * as M from '@/mock/data';

// ==========================================
// 盒子管理相关接口 (Box)
// ==========================================

// 分页获取盒子列表 (OpenAPI: /api/boxes/page)
export const getBoxPage = (params: QueryBoxDTO) => 
  request<PageResult<BoxDTO>>('/api/boxes/page', 'GET', params, M.mockBoxPage);

// 新增盒子
export const addBox = (data: BoxDTO) => 
  request<void>('/api/boxes/add', 'POST', data, M.mockSuccess);

// 更新盒子信息
export const updateBox = (data: BoxDTO) => 
  request<void>('/api/boxes/update', 'PUT', data, M.mockSuccess);

// [新增] 上报盒子状态
export const reportBoxStatus = (data: BoxDTO) => 
  request<void>('/api/boxes/report/status', 'PUT', data, M.mockSuccess);

// *注意*：OpenAPI 中没有 deleteBox 和 getBoxDetail 接口。
// 建议在前端使用 getBoxPage 进行筛选，或确认后端是否遗漏。
// 这里保留 Mock 用于兼容旧逻辑，但在真实调用中可能需要调整。
export const getBoxDetail = (id: number) => 
  request<BoxDTO>(`/api/boxes/${id}`, 'GET', {}, M.mockBoxDetail); 

// ==========================================
// 物品管理相关接口 (Item)
// ==========================================

// 获取物品列表 (无分页)
export const getItemList = (params: QueryItemDTO) => 
  request<ItemDTO[]>('/api/items/list', 'GET', params, M.mockItemList);

// 分页获取物品列表
export const getItemPage = (params: QueryItemDTO) => 
  request<PageResult<ItemDTO>>('/api/items/page', 'GET', params, M.mockItemPage);

// 新增物品
export const addItem = (data: ItemDTO) => 
  request<void>('/api/items/add', 'POST', data, M.mockSuccess);

// 更新物品
export const updateItem = (data: ItemDTO) => 
  request<void>('/api/items/update', 'PUT', data, M.mockSuccess);

// [新增] 移动物品到另一个盒子
export const moveItem = (itemId: number, boxId: number) => 
  request<void>(`/api/items/move/${itemId}/to/${boxId}`, 'PUT', {}, M.mockSuccess);

// *注意*：OpenAPI 中没有 deleteItem 接口。
export const getItemDetail = (id: number) => 
  request<ItemDTO>(`/api/items/detail/${id}`, 'GET', {}, M.mockItemDetail);

// ==========================================
// 情感标签管理 (Emotion) - [新增]
// ==========================================

// 添加情感标签
export const addEmotion = (data: EmotionDTO) => 
  request<void>('/api/emotions/add', 'POST', data, M.mockSuccess);

// 分页查询情感标签
export const getEmotionPage = (params: QueryEmotionDTO) => 
  request<PageResult<EmotionDTO>>('/api/emotions/page', 'GET', params, M.mockEmotionPage);

// ==========================================
// 用户与AI相关接口 (User & Chat)
// ==========================================

// 用户登录
export const login = (data: LoginRequest) => 
  request<any>('/api/users/login', 'POST', data, M.mockUser);

// 用户注册
export const registerUser = (data: UserDO) => 
  request<UserDTO>('/api/users', 'POST', data, M.mockUser);

// 获取所有用户
export const getUserList = () => 
  request<UserDTO[]>('/api/users', 'GET', {}, M.mockUserList);

// 分页查询用户
export const getUserPage = (params: any) => 
  request<PageResult<UserDTO>>('/api/users/page', 'GET', params, M.mockUserPage);

// AI 对话
export const sendChat = (data: AiChatRequestDTO) => 
  request<AiChatResponse>('/api/chat', 'POST', data, M.mockChat);

// ==========================================
// 错误修复管理 (Error Recovery) - [新增]
// ==========================================

// 记录运行时错误
export const recordError = (data: ErrorLogDTO) => 
  request<void>('/api/error-recovery/record-error', 'POST', data, M.mockSuccess);

// 触发优雅降级
export const triggerFallback = (data: FallbackRequestDTO) => 
  request<any>('/api/error-recovery/trigger-fallback', 'POST', data, M.mockSuccess);