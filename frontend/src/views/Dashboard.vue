<template>
  <div class="dashboard">
    <div class="grid grid-cols-4 gap-6 mb-6">
      <Card class="kpi-card">
        <div class="kpi-content">
          <div class="kpi-value">{{ kpi.todayProduction }}</div>
          <div class="kpi-label">오늘 생산량</div>
        </div>
        <div class="kpi-icon">📦</div>
      </Card>

      <Card class="kpi-card">
        <div class="kpi-content">
          <div class="kpi-value">{{ kpi.activeWorks }}</div>
          <div class="kpi-label">진행 중 작업</div>
        </div>
        <div class="kpi-icon">⚡</div>
      </Card>

      <Card class="kpi-card">
        <div class="kpi-content">
          <div class="kpi-value">{{ kpi.equipmentErrors }}</div>
          <div class="kpi-label">설비 이상 수</div>
        </div>
        <div class="kpi-icon">⚠️</div>
      </Card>

      <Card class="kpi-card">
        <div class="kpi-content">
          <div class="kpi-value">{{ kpi.lowStockItems }}</div>
          <div class="kpi-label">재고 부족 품목</div>
        </div>
        <div class="kpi-icon">📉</div>
      </Card>
    </div>

    <div class="grid grid-cols-2 gap-6 mb-6">
      <Card title="설비 상태 요약">
        <div class="equipment-status">
          <div class="status-item">
            <Badge variant="success">가동</Badge>
            <span>{{ equipmentStatusCounts.active }}대</span>
          </div>
          <div class="status-item">
            <Badge variant="secondary">정지</Badge>
            <span>{{ equipmentStatusCounts.stopped }}대</span>
          </div>
          <div class="status-item">
            <Badge variant="warning">점검</Badge>
            <span>{{ equipmentStatusCounts.maintenance }}대</span>
          </div>
          <div class="status-item">
            <Badge variant="error">이상</Badge>
            <span>{{ equipmentStatusCounts.error }}대</span>
          </div>
        </div>
      </Card>

      <Card title="재고 부족 알림">
        <div v-if="lowStockMaterials.length === 0" class="no-data">
          재고 부족 품목이 없습니다.
        </div>
        <div v-else class="stock-alerts">
          <div
            v-for="material in lowStockMaterials"
            :key="material.id"
            class="alert-item"
          >
            <div class="alert-content">
              <div class="alert-name">{{ material.name }}</div>
              <div class="alert-stock">
                현재: {{ material.currentStock }}{{ material.unit }} / 안전재고:
                {{ material.safetyStock }}{{ material.unit }}
              </div>
            </div>
            <Badge
              :variant="material.status === 'critical' ? 'error' : 'warning'"
            >
              {{ material.status === "critical" ? "긴급" : "부족" }}
            </Badge>
          </div>
        </div>
      </Card>
    </div>

    <div class="grid grid-cols-3 gap-6 mb-6">
      <Card title="생산량 추이">
        <div ref="productionChartRef" style="height: 250px"></div>
      </Card>
      <Card title="설비 상태 현황">
        <div ref="equipmentChartRef" style="height: 250px"></div>
      </Card>
      <Card title="수율 추이">
        <div ref="yieldChartRef" style="height: 250px"></div>
      </Card>
      <Card title="작업 지시 현황">
        <div ref="workOrderRef" style="height: 250px"></div>
      </Card>
    </div>

    <div class="grid grid-cols-2 gap-6">
      <Card title="최근 작업 지시">
        <DataTable :columns="workOrderColumns" :data="recentWorkOrders">
          <template #cell-status="{ value }">
            <Badge :variant="getWorkOrderStatusVariant(value)">
              {{ getWorkOrderStatusText(value) }}
            </Badge>
          </template>
        </DataTable>
      </Card>

      <Card title="최근 생산 실적">
        <DataTable :columns="productionColumns" :data="recentProduction">
          <template #cell-yieldRate="{ value }">
            <span
              :class="
                value >= 98
                  ? 'text-green-600'
                  : value >= 95
                  ? 'text-yellow-600'
                  : 'text-red-600'
              "
            >
              {{ value }}%
            </span>
          </template>
        </DataTable>
      </Card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import * as echarts from "echarts";
import Card from "@/components/common/Card.vue";
import Badge from "@/components/common/Badge.vue";
import DataTable from "@/components/common/DataTable.vue";
import { ApiService } from "@/services/api";
import type {
  DashboardKPI,
  WorkOrder,
  ProductionResult,
  Material,
  Equipment,
} from "@/types";

const kpi = ref<DashboardKPI>({
  todayProduction: 0,
  activeWorks: 0,
  equipmentErrors: 0,
  lowStockItems: 0,
});

const productionChartRef = ref<HTMLElement | null>(null);
const equipmentChartRef = ref<HTMLElement | null>(null);
const yieldChartRef = ref<HTMLElement | null>(null);
const workOrderRef = ref<HTMLElement | null>(null);

const recentWorkOrders = ref<WorkOrder[]>([]);
const recentProduction = ref<ProductionResult[]>([]);
const lowStockMaterials = ref<Material[]>([]);
const equipmentList = ref<Equipment[]>([]);

const workOrderColumns = [
  { key: "orderNumber", label: "지시번호" },
  { key: "productName", label: "제품명" },
  { key: "targetQuantity", label: "목표수량" },
  { key: "status", label: "상태" },
];

const productionColumns = [
  { key: "resultNumber", label: "실적번호" },
  { key: "productName", label: "제품명" },
  { key: "productionQuantity", label: "생산수량" },
  { key: "yieldRate", label: "수율" },
];

const equipmentStatusCounts = computed(() => {
  const counts = {
    active: 0,
    stopped: 0,
    maintenance: 0,
    error: 0,
  };

  equipmentList.value.forEach((equipment: Equipment) => {
    counts[equipment.status as keyof typeof counts]++;
  });

  return counts;
});

const getWorkOrderStatusVariant = (status: string) => {
  switch (status) {
    case "completed":
      return "success";
    case "in_progress":
      return "info";
    case "waiting":
      return "secondary";
    case "cancelled":
      return "error";
    default:
      return "secondary";
  }
};

const getWorkOrderStatusText = (status: string) => {
  switch (status) {
    case "completed":
      return "완료";
    case "in_progress":
      return "진행중";
    case "waiting":
      return "대기";
    case "cancelled":
      return "취소";
    default:
      return status;
  }
};

const loadDashboardData = async () => {
  try {
    const [
      kpiData,
      workOrders,
      production,
      materials,
      equipment,
      dailyProduction,
      equipmentStatus,
      yieldTrend,
      WorkOrderStatus,
    ] = await Promise.all([
      ApiService.getDashboardKPI(),
      ApiService.getRecentWorkOrders(),
      ApiService.getRecentProductionResults(),
      ApiService.getLowStockMaterials(),
      ApiService.getEquipmentList(),
      ApiService.getDailyProduction(),
      ApiService.getEquipmentStatus(),
      ApiService.getYieldTrend(),
      ApiService.getWorkOrderStatus(),
    ]);

    kpi.value = kpiData;
    recentWorkOrders.value = workOrders;
    recentProduction.value = production;
    lowStockMaterials.value = materials;
    equipmentList.value = equipment;

    initProductionChart(dailyProduction);
    initEquipmentChart(equipmentStatus);
    initYieldChart(yieldTrend);
    initWorkOrderChart(WorkOrderStatus);
  } catch (error) {
    console.error("Dashboard 데이터 로드 실패:", error);
  }
};

const initProductionChart = (data: { date: string; quantity: number }[]) => {
  if (!productionChartRef.value) return;
  const chart = echarts.init(productionChartRef.value);
  chart.setOption({
    tooltip: { trigger: "axis" },
    xAxis: { type: "category", data: data.map((d) => d.date) },
    yAxis: { type: "value", name: "생산량" },
    series: [
      {
        type: "bar",
        data: data.map((d) => d.quantity),
        itemStyle: { color: "#3b82f6" },
      },
    ],
  });
};

const initEquipmentChart = (data: Record<string, number>) => {
  if (!equipmentChartRef.value) return;
  const chart = echarts.init(equipmentChartRef.value);
  chart.setOption({
    tooltip: { trigger: "item", formatter: "{b}: {c}대 ({d}%)" },
    legend: { bottom: 0, itemWidth: 12, itemHeight: 12 },
    series: [
      {
        type: "pie",
        radius: ["40%", "65%"],
        center: ["50%", "45%"],
        label: { show: false },
        labelLine: { show: false },
        data: [
          { value: data.active, name: "가동", itemStyle: { color: "#10b981" } },
          {
            value: data.stopped,
            name: "정지",
            itemStyle: { color: "#6b7280" },
          },
          {
            value: data.maintenance,
            name: "점검",
            itemStyle: { color: "#f59e0b" },
          },
          { value: data.error, name: "이상", itemStyle: { color: "#ef4444" } },
        ],
      },
    ],
  });
};

const initYieldChart = (data: { date: string; yieldRate: number }[]) => {
  if (!yieldChartRef.value) return;
  const chart = echarts.init(yieldChartRef.value);
  chart.setOption({
    tooltip: { trigger: "axis", formatter: "{b}: {c}%" },
    xAxis: { type: "category", data: data.map((d) => d.date) },
    yAxis: { type: "value", name: "수율(%)", min: 90, max: 100 },
    series: [
      {
        type: "line",
        data: data.map((d) => d.yieldRate.toFixed(1)),
        smooth: true,
        itemStyle: { color: "#10b981" },
        areaStyle: { color: "rgba(16, 185, 129, 0.1)" },
      },
    ],
  });
};

const initWorkOrderChart = (data: Record<string, number>) => {
  if (!workOrderRef.value) return;
  const chart = echarts.init(workOrderRef.value);
  chart.setOption({
    tooltip: { trigger: "item", formatter: "{b}: {c}개 ({d}%)" },
    legend: { bottom: 0, itemWidth: 12, itemHeight: 12 },
    series: [
      {
        type: "pie",
        radius: ["40%", "65%"],
        center: ["50%", "45%"],
        label: { show: false },
        labelLine: { show: false },
        data: [
          {
            value: data.in_progress,
            name: "진행중",
            itemStyle: { color: "#10b981" },
          },
          {
            value: data.completed,
            name: "완료",
            itemStyle: { color: "#6b7280" },
          },
          {
            value: data.waiting,
            name: "대기",
            itemStyle: { color: "#f59e0b" },
          },
          {
            value: data.cancelled,
            name: "취소",
            itemStyle: { color: "#ef4444" },
          },
        ],
      },
    ],
  });
};

onMounted(() => {
  loadDashboardData();
});
</script>

<style scoped>
.dashboard {
  max-width: 1400px;
}

.kpi-card {
  background: linear-gradient(
    135deg,
    var(--bg-primary) 0%,
    var(--bg-secondary) 100%
  );
}

.kpi-content {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.kpi-value {
  font-size: 2rem;
  font-weight: 700;
  color: var(--primary-color);
  line-height: 1;
}

.kpi-label {
  font-size: 0.875rem;
  color: var(--text-secondary);
  margin-top: 0.5rem;
}

.kpi-icon {
  font-size: 2rem;
  opacity: 0.7;
  justify-self: end;
  align-self: center;
}

.kpi-card .card-body {
  display: grid;
  grid-template-columns: 1fr auto;
  align-items: center;
  padding: 1.5rem;
  min-height: 100px;
  gap: 1rem;
}

.equipment-status {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.status-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.75rem;
  background-color: var(--bg-secondary);
  border-radius: 0.375rem;
}

.stock-alerts {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.alert-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.75rem;
  background-color: var(--bg-secondary);
  border-radius: 0.375rem;
}

.alert-content {
  flex: 1;
}

.alert-name {
  font-weight: 500;
  margin-bottom: 0.25rem;
}

.alert-stock {
  font-size: 0.75rem;
  color: var(--text-secondary);
}

.no-data {
  text-align: center;
  color: var(--text-secondary);
  padding: 2rem;
}

.text-green-600 {
  color: var(--success-color);
}
.text-yellow-600 {
  color: var(--warning-color);
}
.text-red-600 {
  color: var(--error-color);
}
</style>
