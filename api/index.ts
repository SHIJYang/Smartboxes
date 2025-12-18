import { request } from '@/utils/request';
import type { 
  BoxDTO, 
  ItemDTO, 
  UserDTO, 
  AiChatResponse, 
  AiChatRequestDTO, 
  LoginRequest,
  QueryBoxDTO,
  QueryItemDTO,
  UserDO 
} from '@/common/types';
import * as M from '@/mock/data';

// ==========================================
// 盒子管理相关接口 (Box)
// ==========================================

// 获取盒子列表
export const getBoxList = (params: QueryBoxDTO) => 
  request<BoxDTO[]>('/api/boxes/list', 'GET', params, M.mockBoxList);

// [关键修复] 获取盒子详情
// 用于 boxdetail.vue 和 boxedit.vue
export const getBoxDetail = (id: number) => 
  request<BoxDTO>(`/api/boxes/${id}`, 'GET', {}, M.mockBoxDetail);

// 新增或更新盒子
export const saveBox = (data: BoxDTO) => 
  request<void>(data.id ? '/api/boxes/update' : '/api/boxes/add', data.id ? 'PUT' : 'POST', data, M.mockSuccess);

// 删除盒子
export const deleteBox = (id: number) => 
  request<void>(`/api/boxes/delete/${id}`, 'DELETE', {}, M.mockSuccess);


// ==========================================
// 物品管理相关接口 (Item)
// ==========================================

// 获取物品列表
export const getItemList = (params: QueryItemDTO) => 
  request<ItemDTO[]>('/api/items/list', 'GET', params, M.mockItemList);

// [建议修复] 获取物品详情
// 用于 itemedit.vue
export const getItemDetail = (id: number) => 
  request<ItemDTO>(`/api/items/${id}`, 'GET', {}, M.mockItemDetail);

// 新增或更新物品
export const saveItem = (data: ItemDTO) => 
  request<void>(data.id ? '/api/items/update' : '/api/items/add', data.id ? 'PUT' : 'POST', data, M.mockSuccess);

// 删除物品
export const deleteItem = (id: number) => 
  request<void>(`/api/items/delete/${id}`, 'DELETE', {}, M.mockSuccess);


// ==========================================
// 用户与AI相关接口 (User & Chat)
// ==========================================

// 用户登录
export const login = (data: LoginRequest) => 
  request<any>('/api/users/login', 'POST', data, M.mockUser);

// 用户注册 (用于 register.vue)
export const registerUser = (data: UserDO) => 
  request<UserDTO>('/api/users', 'POST', data, M.mockUser);

// 获取用户详情
export const getUserInfo = (id: number) => 
  request<UserDTO>(`/api/users/${id}`, 'GET', {}, M.mockUser);

// AI 对话
export const sendChat = (data: AiChatRequestDTO) => 
  request<AiChatResponse>('/api/chat', 'POST', data, M.mockChat);