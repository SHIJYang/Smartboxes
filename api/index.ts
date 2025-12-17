import { request } from '@/utils/request';
import type { 
  BoxDTO, 
  ItemDTO, 
  UserDTO, 
  AiChatResponse, 
  AiChatRequestDTO, 
  LoginRequest,
  QueryBoxDTO,
  QueryItemDTO
} from '@/common/types';
import * as M from '@/mock/data';

// --- 盒子相关 ---

// 修复: 使用 QueryBoxDTO 类型
export const getBoxList = (params: QueryBoxDTO) => 
  request<BoxDTO[]>('/api/boxes/list', 'GET', params, M.mockBoxList);

// 警告: OpenApi中没有 /api/boxes/{id} 接口，建议后端补充或通过List筛选
// export const getBoxDetail = (id: number) => request<BoxDTO>(`/api/boxes/${id}`, 'GET', {}, M.mockBoxDetail);

export const saveBox = (data: BoxDTO) => 
  request<void>(data.id ? '/api/boxes/update' : '/api/boxes/add', data.id ? 'PUT' : 'POST', data, M.mockSuccess);

export const deleteBox = (id: number) => 
  request<void>(`/api/boxes/delete/${id}`, 'DELETE', {}, M.mockSuccess);


// --- 物品相关 ---

// 修复: 使用 QueryItemDTO 类型
export const getItemList = (params: QueryItemDTO) => 
  request<ItemDTO[]>('/api/items/list', 'GET', params, M.mockItemList);

// 警告: OpenApi中没有 /api/items/{id} 接口
// export const getItemDetail = (id: number) => request<ItemDTO>(`/api/items/${id}`, 'GET', {}, M.mockItemDetail);

export const saveItem = (data: ItemDTO) => 
  request<void>(data.id ? '/api/items/update' : '/api/items/add', data.id ? 'PUT' : 'POST', data, M.mockSuccess);

export const deleteItem = (id: number) => 
  request<void>(`/api/items/delete/${id}`, 'DELETE', {}, M.mockSuccess);


// --- 用户与AI ---

// 修复: 原 login 方法是 GET /users/{id} (获取详情)，现改为真实的 POST 登录
export const login = (data: LoginRequest) => 
  request<any>('/api/users/login', 'POST', data, M.mockUser);

// 新增: 获取用户详情 (对应原来的 GET /api/users/{id})
export const getUserInfo = (id: number) => 
  request<UserDTO>(`/api/users/${id}`, 'GET', {}, M.mockUser);

export const sendChat = (data: AiChatRequestDTO) => 
  request<AiChatResponse>('/api/chat', 'POST', data, M.mockChat);