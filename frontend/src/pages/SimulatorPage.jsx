function SimulatorPage({
  simulatorForm,
  simulatorResponse,
  handleSimulatorChange,
  handleSimulatorPreset,
  handleSimulatorSubmit,
}) {
  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1>Protected API Simulator</h1>
          <p>
            Send test traffic to the protected API and observe firewall decisions
          </p>
        </div>
      </div>

      <div className="simulator-layout">
        <section className="panel">
          <div className="panel-header">
            <h2>Attack Presets</h2>
            <span>Generate realistic test payloads</span>
          </div>

          <div className="preset-grid">
            <button
              className="preset-card"
              onClick={() => handleSimulatorPreset("NORMAL")}
            >
              <strong>Normal Login</strong>
              <span>Allowed request</span>
            </button>

            <button
              className="preset-card danger"
              onClick={() => handleSimulatorPreset("SQL_INJECTION")}
            >
              <strong>SQL Injection</strong>
              <span>Blocked by inspection</span>
            </button>

            <button
              className="preset-card danger"
              onClick={() => handleSimulatorPreset("XSS")}
            >
              <strong>XSS Attempt</strong>
              <span>Script payload detection</span>
            </button>

            <button
              className="preset-card warning"
              onClick={() => handleSimulatorPreset("ADMIN_LOGIN")}
            >
              <strong>Suspicious Admin</strong>
              <span>Allowed with alert</span>
            </button>

            <button
              className="preset-card blocked"
              onClick={() => handleSimulatorPreset("BLOCKED_IP")}
            >
              <strong>Blocked IP Test</strong>
              <span>Requires active mitigation</span>
            </button>
          </div>
        </section>

        <section className="panel">
          <div className="panel-header">
            <h2>Protected Login Request</h2>
            <span>POST /api/protected/login</span>
          </div>

          <form className="simulator-form" onSubmit={handleSimulatorSubmit}>
            <div className="filter-field">
              <label>Source IP</label>
              <input
                type="text"
                name="sourceIp"
                value={simulatorForm.sourceIp}
                onChange={handleSimulatorChange}
                required
              />
            </div>

            <div className="filter-field">
              <label>Username</label>
              <input
                type="text"
                name="username"
                value={simulatorForm.username}
                onChange={handleSimulatorChange}
                required
              />
            </div>

            <div className="filter-field">
              <label>Password</label>
              <input
                type="text"
                name="password"
                value={simulatorForm.password}
                onChange={handleSimulatorChange}
                required
              />
            </div>

            <button className="dark-button" type="submit">
              Send Protected Request
            </button>
          </form>
        </section>
      </div>

      <section className="panel simulator-response-panel">
        <div className="panel-header">
          <h2>Firewall Decision</h2>
          <span>Runtime response</span>
        </div>

        {simulatorResponse ? (
          <div className="firewall-response">
            <div
              className={
                simulatorResponse.decision === "BLOCKED"
                  ? "decision-banner blocked-decision"
                  : simulatorResponse.decision === "ALLOWED_WITH_ALERT"
                  ? "decision-banner warning-decision"
                  : "decision-banner allowed-decision"
              }
            >
              {simulatorResponse.decision}
            </div>

            <div className="detail-grid">
              <div className="detail-item">
                <span>Reason</span>
                <strong>{simulatorResponse.reason}</strong>
              </div>

              <div className="detail-item">
                <span>Source IP</span>
                <strong>{simulatorResponse.sourceIp}</strong>
              </div>

              <div className="detail-item">
                <span>Event Type</span>
                <strong>{simulatorResponse.eventType}</strong>
              </div>

              <div className="detail-item">
                <span>Risk Score</span>
                <strong>{simulatorResponse.riskScore}</strong>
              </div>
            </div>
          </div>
        ) : (
          <div className="empty-detail simulator-empty">
            <div className="empty-circle"></div>
            <h3>No request sent yet</h3>
            <p>Select a preset or enter custom values, then send a request.</p>
          </div>
        )}
      </section>
    </div>
  );
}

export default SimulatorPage;