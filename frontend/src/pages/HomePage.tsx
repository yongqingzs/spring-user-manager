import React from 'react';
import { Card, Row, Col, Statistic } from 'antd';
import { UserOutlined, TeamOutlined } from '@ant-design/icons';

const HomePage: React.FC = () => {
  return (
    <div>
      <h1 style={{ marginBottom: 24 }}>系统概览</h1>
      
      <Row gutter={16}>
        <Col span={8}>
          <Card>
            <Statistic
              title="用户总数"
              value={112}
              prefix={<UserOutlined />}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic
              title="部门总数"
              value={8}
              prefix={<TeamOutlined />}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic
              title="活跃用户"
              value={93}
              prefix={<UserOutlined />}
            />
          </Card>
        </Col>
      </Row>

      <Card title="欢迎使用用户部门管理系统" style={{ marginTop: 24 }}>
        <p>这是一个基于 Spring Boot + React 的用户部门管理系统。</p>
        <p>您可以通过导航栏访问：</p>
        <ul>
          <li>用户管理：管理系统用户</li>
          <li>部门管理：管理组织架构</li>
        </ul>
      </Card>
    </div>
  );
};

export default HomePage;
