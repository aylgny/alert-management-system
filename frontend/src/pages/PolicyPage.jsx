function PolicyPage({
  policies,
  handlePolicyModeChange,
  handlePolicyRiskScoreChange,
}) {
  const getModeClass = (mode) => {
    if (mode === "BLOCK") {
      return "policy-mode-badge block";
    }

    if (mode === "DETECT_ONLY") {
      return "policy-mode-badge detect";
    }

    return "policy-mode-badge disabled";
  };

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1>Policy</h1>
          <p>Configure detection, enforcement behavior, and risk scoring</p>
        </div>
      </div>

      <section className="panel">
        <div className="panel-header">
          <h2>Firewall Policy Rules</h2>
          <span>{policies.length} rule(s)</span>
        </div>

        <div className="table-scroll">
          <table className="events-table policy-table">
            <thead>
              <tr>
                <th>Event Type</th>
                <th>Current Mode</th>
                <th>Risk Score</th>
                <th>Description</th>
                <th>Change Mode</th>
                <th>Change Risk</th>
              </tr>
            </thead>

            <tbody>
              {policies.map((policy) => (
                <tr key={policy.id}>
                  <td>{policy.eventType}</td>

                  <td>
                    <span className={getModeClass(policy.mode)}>
                      {policy.mode}
                    </span>
                  </td>

                  <td>{policy.riskScore}</td>

                  <td className="policy-description-cell">
                    {policy.description}
                  </td>

                  <td>
                    <select
                      className="status-select policy-select"
                      value={policy.mode}
                      onChange={(event) =>
                        handlePolicyModeChange(policy, event.target.value)
                      }
                    >
                      <option value="BLOCK">BLOCK</option>
                      <option value="DETECT_ONLY">DETECT_ONLY</option>
                      <option value="DISABLED">DISABLED</option>
                    </select>
                  </td>

                  <td>
                    <input
                      className="policy-risk-input"
                      type="number"
                      min="0"
                      max="100"
                      value={policy.riskScore}
                      onChange={(event) =>
                        handlePolicyRiskScoreChange(policy, event.target.value)
                      }
                    />
                  </td>
                </tr>
              ))}

              {policies.length === 0 && (
                <tr>
                  <td colSpan="6" className="empty-table-message">
                    No policy rules found.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </section>

      <section className="panel policy-help-panel">
        <div className="panel-header">
          <h2>Mode Behavior</h2>
          <span>How policies affect protected traffic</span>
        </div>

        <div className="policy-help-grid">
          <div className="policy-help-card">
            <span className="policy-mode-badge block">BLOCK</span>
            <p>Creates an alert and blocks the protected request with 403.</p>
          </div>

          <div className="policy-help-card">
            <span className="policy-mode-badge detect">DETECT_ONLY</span>
            <p>Creates an alert but allows the protected request.</p>
          </div>

          <div className="policy-help-card">
            <span className="policy-mode-badge disabled">DISABLED</span>
            <p>Allows the request without creating a security alert.</p>
          </div>
        </div>
      </section>

      <section className="panel policy-help-panel">
        <div className="panel-header">
          <h2>Risk Score Meaning</h2>
          <span>Used for severity and reputation scoring</span>
        </div>

        <div className="policy-help-grid">
          <div className="policy-help-card">
            <span className="policy-risk-pill critical-risk">90 - 100</span>
            <p>Critical risk. Reputation status usually becomes MALICIOUS.</p>
          </div>

          <div className="policy-help-card">
            <span className="policy-risk-pill high-risk">70 - 89</span>
            <p>High risk. Reputation status usually becomes SUSPICIOUS.</p>
          </div>

          <div className="policy-help-card">
            <span className="policy-risk-pill low-risk">0 - 69</span>
            <p>Lower risk. Reputation status usually stays MONITORED.</p>
          </div>
        </div>
      </section>
    </div>
  );
}

export default PolicyPage;