/**
 * Design System - 主入口
 *
 * 用法：
 * import { Button, ListRow, useTheme } from '@/design-system';
 */

// 初始化
export { initDesignSystem } from './init';

// Design Tokens
export * from './tokens';

// Composables
export { useTheme, type Theme } from './composables/useTheme';

// PrimeVue 配置
export { primeVueConfig, primeVuePassThrough } from './primevue';

// Primitives (原子组件)
export {
  Button,
  Input,
  Select,
  Checkbox,
  Switch,
  Divider,
  Modal,
  EmptyState,
  ProgressRing,
  Avatar,
  Alert,
  Tag,
  Timeline,
  TimelineItem,
} from './primitives';

// Patterns (模式组件)
export {
  PageLayout,
  PageHeader,
  Section,
  ListRow,
  DescriptionList,
  FormLayout,
  FormItem,
  InsetGroup,
  InsetItem,
  AuthLayout,
} from './patterns';
