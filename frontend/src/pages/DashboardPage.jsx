import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid,
} from "recharts";

function DashboardPage({ summary, severityData, alerts }) {
  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1>Dashboard</h1>
          <p>
            Live overview of threat telemetry, active alerts, and security
            activity
          </p>
        </div>

        <button className="dark-button" onClick={() => window.location.reload()}>
          Refresh
        </button>
      </div>

      {summary && (
        <div className="stats-grid">
          <div className="stat-card">
            <span>Total Alerts</span>
            <strong>{summary.totalAlerts}</strong>
            <p>Persisted security alerts</p>
          </div>

          <div className="stat-card">
            <span>Critical Alerts</span>
            <strong>{summary.criticalAlerts}</strong>
            <p>Highest priority threats</p>
          </div>

          <div className="stat-card">
            <span>High Alerts</span>
            <strong>{summary.highAlerts}</strong>
            <p>Requires analyst review</p>
          </div>

          <div className="stat-card">
            <span>New Alerts</span>
            <strong>{summary.newAlerts}</strong>
            <p>Unresolved incoming events</p>
          </div>

          <div className="stat-card">
            <span>Average Risk</span>
            <strong>{summary.averageRiskScore.toFixed(1)}</strong>
            <p>Mean alert risk score</p>
          </div>
        </div>
      )}

      <div className="dashboard-grid">
        <section className="panel">
          <div className="panel-header">
            <h2>Severity Distribution</h2>
            <span>Alert risk levels</span>
          </div>

          <div className="chart-wrapper">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={severityData} barSize={42}>
                <CartesianGrid vertical={false} stroke="#e2e8f0" />
                <XAxis
                  dataKey="label"
                  tick={{ fill: "#64748b", fontSize: 12 }}
                  axisLine={false}
                  tickLine={false}
                />
                <YAxis
                  allowDecimals={false}
                  tick={{ fill: "#64748b", fontSize: 12 }}
                  axisLine={false}
                  tickLine={false}
                />
                <Tooltip />
                <Bar dataKey="count" fill="#0f766e" radius={[8, 8, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </section>

        <section className="panel">
          <div className="panel-header">
            <h2>Recent Events</h2>
            <span>{alerts.length} item(s)</span>
          </div>

          <table className="mini-table">
            <thead>
              <tr>
                <th>Source IP</th>
                <th>Event</th>
                <th>Severity</th>
                <th>Risk</th>
              </tr>
            </thead>

            <tbody>
              {alerts.slice(0, 5).map((alert) => (
                <tr key={alert.id}>
                  <td>{alert.sourceIp}</td>
                  <td>{alert.eventType}</td>
                  <td>
                    <span className={`badge ${alert.severity.toLowerCase()}`}>
                      {alert.severity}
                    </span>
                  </td>
                  <td>{alert.riskScore}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>
      </div>
    </div>
  );
}

export default DashboardPage;