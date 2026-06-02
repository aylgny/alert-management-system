import { useMemo, useState } from "react";

function EventsPage({
  alerts,
  handleStatusChange,
  logForm,
  handleLogFormChange,
  handleLogSubmit,
  handleBlockSourceFromAlert,
}) {
  const [selectedAlert, setSelectedAlert] = useState(null);

  const [filters, setFilters] = useState({
    sourceIp: "",
    eventType: "ALL",
    severity: "ALL",
    status: "ALL",
  });

  const handleFilterChange = (event) => {
    const { name, value } = event.target;

    setFilters({
      ...filters,
      [name]: value,
    });
  };

  const filteredAlerts = useMemo(() => {
    return alerts.filter((alert) => {
      const matchesSourceIp =
        filters.sourceIp === "" ||
        alert.sourceIp.toLowerCase().includes(filters.sourceIp.toLowerCase());

      const matchesEventType =
        filters.eventType === "ALL" || alert.eventType === filters.eventType;

      const matchesSeverity =
        filters.severity === "ALL" || alert.severity === filters.severity;

      const matchesStatus =
        filters.status === "ALL" || alert.status === filters.status;

      return (
        matchesSourceIp &&
        matchesEventType &&
        matchesSeverity &&
        matchesStatus
      );
    });
  }, [alerts, filters]);

  const activeAlert = selectedAlert || filteredAlerts[0];

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1>Events Explorer</h1>
          <p>Search, review, and inspect security alerts from the control plane</p>
        </div>
      </div>

      <section className="panel filters-panel">
        <div className="panel-header">
          <h2>Filters</h2>
          <button
            className="light-button"
            onClick={() =>
              setFilters({
                sourceIp: "",
                eventType: "ALL",
                severity: "ALL",
                status: "ALL",
              })
            }
          >
            Reset
          </button>
        </div>

        <div className="filters-grid">
          <div className="filter-field">
            <label>Source IP</label>
            <input
              type="text"
              name="sourceIp"
              placeholder="185.23.45.10"
              value={filters.sourceIp}
              onChange={handleFilterChange}
            />
          </div>

          <div className="filter-field">
            <label>Event Type</label>
            <select
              name="eventType"
              value={filters.eventType}
              onChange={handleFilterChange}
            >
              <option value="ALL">Any</option>
              <option value="FAILED_LOGIN">FAILED_LOGIN</option>
              <option value="PORT_SCAN">PORT_SCAN</option>
              <option value="HONEYPOT_ACCESS">HONEYPOT_ACCESS</option>
              <option value="ADMIN_LOGIN">ADMIN_LOGIN</option>
              <option value="FORBIDDEN_ACCESS">FORBIDDEN_ACCESS</option>
              <option value="SQL_INJECTION">SQL_INJECTION</option>
              <option value="XSS_ATTEMPT">XSS_ATTEMPT</option>
              <option value="ACTIVE_MITIGATION">ACTIVE_MITIGATION</option>
            </select>
          </div>

          <div className="filter-field">
            <label>Severity</label>
            <select
              name="severity"
              value={filters.severity}
              onChange={handleFilterChange}
            >
              <option value="ALL">Any</option>
              <option value="LOW">LOW</option>
              <option value="MEDIUM">MEDIUM</option>
              <option value="HIGH">HIGH</option>
              <option value="CRITICAL">CRITICAL</option>
            </select>
          </div>

          <div className="filter-field">
            <label>Status</label>
            <select
              name="status"
              value={filters.status}
              onChange={handleFilterChange}
            >
              <option value="ALL">Any</option>
              <option value="NEW">NEW</option>
              <option value="INVESTIGATING">INVESTIGATING</option>
              <option value="RESOLVED">RESOLVED</option>
              <option value="FALSE_POSITIVE">FALSE_POSITIVE</option>
            </select>
          </div>
        </div>
      </section>

      <section className="panel log-ingest-panel">
        <div className="panel-header">
          <h2>Ingest Security Log</h2>
          <span>Create alert from raw event</span>
        </div>

        <form className="log-ingest-form" onSubmit={handleLogSubmit}>
          <div className="log-form-grid">
            <div className="filter-field">
              <label>Source IP</label>
              <input
                type="text"
                name="sourceIp"
                placeholder="45.88.12.90"
                value={logForm.sourceIp}
                onChange={handleLogFormChange}
                required
              />
            </div>

            <div className="filter-field">
              <label>Target IP</label>
              <input
                type="text"
                name="targetIp"
                placeholder="10.0.0.3"
                value={logForm.targetIp}
                onChange={handleLogFormChange}
                required
              />
            </div>

            <div className="filter-field">
              <label>Event Type</label>
              <select
                name="eventType"
                value={logForm.eventType}
                onChange={handleLogFormChange}
              >
                <option value="FAILED_LOGIN">FAILED_LOGIN</option>
                <option value="PORT_SCAN">PORT_SCAN</option>
                <option value="HONEYPOT_ACCESS">HONEYPOT_ACCESS</option>
                <option value="ADMIN_LOGIN">ADMIN_LOGIN</option>
                <option value="FORBIDDEN_ACCESS">FORBIDDEN_ACCESS</option>
                <option value="SQL_INJECTION">SQL_INJECTION</option>
                <option value="XSS_ATTEMPT">XSS_ATTEMPT</option>
              </select>
            </div>

            <div className="filter-field">
              <label>Source System</label>
              <input
                type="text"
                name="sourceSystem"
                placeholder="Linux Server"
                value={logForm.sourceSystem}
                onChange={handleLogFormChange}
                required
              />
            </div>
          </div>

          <div className="filter-field">
            <label>Raw Message</label>
            <textarea
              name="rawMessage"
              placeholder="Failed password for root from 45.88.12.90 port 55822 ssh2"
              value={logForm.rawMessage}
              onChange={handleLogFormChange}
              required
            />
          </div>

          <button className="dark-button" type="submit">
            Ingest Log
          </button>
        </form>
      </section>

      <div className="events-layout">
        <section className="panel">
          <div className="panel-header">
            <h2>Results</h2>
            <span>{filteredAlerts.length} item(s)</span>
          </div>

          <table className="events-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Source IP</th>
                <th>Event Type</th>
                <th>Severity</th>
                <th>Status</th>
                <th>Risk</th>
              </tr>
            </thead>

            <tbody>
              {filteredAlerts.map((alert) => (
                <tr
                  key={alert.id}
                  className={
                    activeAlert && activeAlert.id === alert.id
                      ? "selected-row"
                      : ""
                  }
                  onClick={() => setSelectedAlert(alert)}
                >
                  <td>{alert.id}</td>
                  <td>{alert.sourceIp}</td>
                  <td>{alert.eventType}</td>
                  <td>
                    <span className={`badge ${alert.severity.toLowerCase()}`}>
                      {alert.severity}
                    </span>
                  </td>
                  <td>{alert.status}</td>
                  <td>{alert.riskScore}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>

        <section className="panel detail-panel">
          <div className="panel-header">
            <h2>Event Details</h2>
          </div>

          {activeAlert ? (
            <div className="detail-content">
              <div className="detail-title-row">
                <div>
                  <h3>{activeAlert.title}</h3>
                  <p>{activeAlert.description}</p>
                </div>

                <div className="detail-action-group">
                  <span className={`badge ${activeAlert.severity.toLowerCase()}`}>
                    {activeAlert.severity}
                  </span>

                  <button
                    className="block-source-button"
                    onClick={() => handleBlockSourceFromAlert(activeAlert)}
                  >
                    Block Source IP
                  </button>
                </div>
              </div>

              <div className="detail-grid">
                <div className="detail-item">
                  <span>Alert ID</span>
                  <strong>{activeAlert.id}</strong>
                </div>

                <div className="detail-item">
                  <span>Event Type</span>
                  <strong>{activeAlert.eventType}</strong>
                </div>

                <div className="detail-item">
                  <span>Source IP</span>
                  <strong>{activeAlert.sourceIp}</strong>
                </div>

                <div className="detail-item">
                  <span>Target IP</span>
                  <strong>{activeAlert.targetIp}</strong>
                </div>

                <div className="detail-item">
                  <span>Risk Score</span>
                  <strong>{activeAlert.riskScore}</strong>
                </div>

                <div className="detail-item">
                  <span>Status</span>
                  <select
                    className="status-select"
                    value={activeAlert.status}
                    onChange={(event) => {
                      handleStatusChange(activeAlert.id, event.target.value);

                      setSelectedAlert({
                        ...activeAlert,
                        status: event.target.value,
                      });
                    }}
                  >
                    <option value="NEW">NEW</option>
                    <option value="INVESTIGATING">INVESTIGATING</option>
                    <option value="RESOLVED">RESOLVED</option>
                    <option value="FALSE_POSITIVE">FALSE_POSITIVE</option>
                  </select>
                </div>
              </div>

              <div className="raw-log-box">
                <span>Raw Message</span>
                <p>
                  {activeAlert.rawMessage
                    ? activeAlert.rawMessage
                    : "No raw log message available for this event."}
                </p>
              </div>
            </div>
          ) : (
            <div className="empty-detail">
              <div className="empty-circle"></div>
              <h3>No event selected</h3>
              <p>Select an event from the results table to inspect details.</p>
            </div>
          )}
        </section>
      </div>
    </div>
  );
}

export default EventsPage;