import React, { useEffect, useState } from 'react';
import { Card, Row, Col, Statistic, Spin } from 'antd';
import { UserOutlined, TeamOutlined } from '@ant-design/icons';
import { getDashboardStats, DashboardStats } from '../services/dashboard';

const HomePage: React.FC = () => {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const data = await getDashboardStats();
        setStats(data);
      } catch (error) {
        console.error('Failed to fetch dashboard stats:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchStats();
  }, []);

  if (loading) {
    return <Spin size="large" />;
  }

  return (
    <div>
      <h1 style={{ marginBottom: 24 }}>系统概览</h1>
      
      <Row gutter={16}>
        <Col span={8}>
          <Card>
            <Statistic
              title="用户总数"
              value={stats?.userCount ?? 0}
              prefix={<UserOutlined />}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic
              title="活跃用户"
              value={stats?.activeUserCount ?? 0}
              prefix={<UserOutlined />}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic
              title="部门总数"
              value={stats?.departmentCount ?? 0}
              prefix={<TeamOutlined />}
            />
          </Card>
        </Col>
      </Row>

      {/* <Card title="欢迎使用用户部门管理系统" style={{ marginTop: 24 }}>
        <p>您可以通过导航栏访问：</p>
        <ul>
          <li>用户管理：管理系统用户</li>
          <li>部门管理：管理组织架构</li>
        </ul>
      </Card> */}
    </div>
  );
};

export default HomePage;
