/**
 * 时间格式化工具
 * @param {Number|String|Date} time - 时间戳（毫秒）、日期字符串或Date对象
 * @param {String} format - 格式化模板（默认：'YYYY-MM-DD HH:mm:ss'）
 * @returns {String} 格式化后的时间字符串
 */
export function formatTime(time, format = 'YYYY-MM-DD HH:mm:ss') {
  // 处理时间输入：统一转为Date对象
  let date;
  if (typeof time === 'number' || typeof time === 'string') {
    date = new Date(Number(time));
  } else if (time instanceof Date) {
    date = time;
  } else {
    date = new Date(); // 默认为当前时间
  }

  // 定义时间单位映射
  const timeMap = {
    YYYY: date.getFullYear(),
    MM: padZero(date.getMonth() + 1),
    DD: padZero(date.getDate()),
    HH: padZero(date.getHours()),
    mm: padZero(date.getMinutes()),
    ss: padZero(date.getSeconds()),
  };

  // 替换模板中的时间单位
  return format.replace(/YYYY|MM|DD|HH|mm|ss/g, key => timeMap[key] || key);
}

/**
 * 数字补零（小于10时补0）
 * @param {Number} num - 要补零的数字
 * @returns {String} 补零后的字符串
 */
function padZero(num) {
  return num < 10 ? `0${num}` : `${num}`;
}

/**
 * 相对时间格式化（例如：刚刚、5分钟前、昨天）
 * @param {Number|String|Date} time - 时间戳（毫秒）、日期字符串或Date对象
 * @returns {String} 相对时间描述
 */
export function formatRelativeTime(time) {
  const date = typeof time === 'number' || typeof time === 'string' 
    ? new Date(Number(time)) 
    : time instanceof Date ? time : new Date();
  
  const now = new Date();
  const diff = now - date; // 时间差（毫秒）

  // 计算时间差对应的单位
  const minute = 60 * 1000;
  const hour = 60 * minute;
  const day = 24 * hour;
  const week = 7 * day;

  if (diff < minute) {
    return '刚刚';
  } else if (diff < hour) {
    return `${Math.floor(diff / minute)}分钟前`;
  } else if (diff < day) {
    return `${Math.floor(diff / hour)}小时前`;
  } else if (diff < week) {
    return `${Math.floor(diff / day)}天前`;
  } else {
    // 超过一周则返回格式化日期
    return formatTime(date, 'YYYY-MM-DD HH:mm');
  }
}