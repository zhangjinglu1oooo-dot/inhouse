const { createApp } = Vue;

createApp({
  data() {
    return {
      apiBase: 'http://localhost:8080',
      highlights: [
        {
          title: '项目协作中心',
          description: '跨团队协作、任务拆解与进度自动追踪。',
          icon: '📌',
          action: '进入',
        },
        {
          title: '会议纪要智能化',
          description: '自动生成纪要、行动项与关键决策。',
          icon: '📝',
          action: '开始整理',
        },
        {
          title: '客户洞察看板',
          description: '汇总客户生命周期数据与运营指标。',
          icon: '📊',
          action: '查看',
        },
        {
          title: '知识资产地图',
          description: '组织知识结构化归档与检索。',
          icon: '🧠',
          action: '探索',
        },
      ],
      chips: ['高频推荐', '跨部门协作', '自动化流程', '智能总结'],
      assistants: [
        {
          name: '业务汇报助手',
          summary: '一键生成周报、月报与 OKR 追踪。',
          icon: '📅',
        },
        {
          name: '数据洞察助手',
          summary: '自动识别指标异常并给出建议。',
          icon: '📈',
        },
        {
          name: '客户沟通助手',
          summary: '整理客户需求并生成跟进清单。',
          icon: '💬',
        },
      ],
    };
  },
}).mount('#app');
