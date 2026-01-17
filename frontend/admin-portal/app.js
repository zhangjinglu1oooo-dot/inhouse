const { createApp } = Vue;

createApp({
  data() {
    return {
      stats: [
        { label: '在职员工', value: '2,418', note: '本月新增 36 人' },
        { label: '应用接入', value: '86', note: '通过审批 4 个' },
        { label: '策略数量', value: '312', note: '待审核 7 条' },
        { label: '风险预警', value: '3', note: '高危 1 条' },
      ],
      employeeFields: [
        '员工编号',
        '姓名',
        '邮箱',
        '手机号',
        '部门',
        '职位',
        '直属上级',
        '工位地点',
        '入职日期',
        '状态',
      ],
      audits: [
        { title: '审批通过：客户数据访问策略', meta: '张颖 · 2 分钟前' },
        { title: '角色变更：市场部内容负责人', meta: '系统 · 25 分钟前' },
        { title: '异常提醒：多地登录检测', meta: '安全中心 · 1 小时前' },
      ],
    };
  },
}).mount('#admin');
