function MitigationsPage({
  mitigations,
  mitigationForm,
  handleMitigationFormChange,
  handleMitigationSubmit,
  handleReleaseMitigation,
}) {
  const formatDateTime = (value) => {
    if (!value) {
      return "-";
    }

    return new Date(value).toLocaleString();
  };

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1>Mitigations</h1>
          <p>Manage active source blocks and manual operator actions</p>
        </div>
      </div>

      <div className="mitigations-layout">
        <section className="panel mitigation-form-panel">
          <div className="panel-header">
            <h2>Manual Block</h2>
            <span>Temporarily block a suspicious IP</span>
          </div>

          <form className="mitigation-form" onSubmit={handleMitigationSubmit}>
            <div className="mitigation-form-grid">
              <div className="filter-field">
                <label>Source IP</label>
                <input
                  type="text"
                  name="sourceIp"
                  placeholder="45.88.12.90"
                  value={mitigationForm.sourceIp}
                  onChange={handleMitigationFormChange}
                  required
                />
              </div>

              <div className="filter-field">
                <label>TTL Seconds</label>
                <input
                  type="number"
                  name="ttlSeconds"
                  min="60"
                  value={mitigationForm.ttlSeconds}
                  onChange={handleMitigationFormChange}
                  required
                />
              </div>
            </div>

            <div className="filter-field">
              <label>Reason</label>
              <textarea
                name="reason"
                placeholder="Suspicious admin login attempts from unknown source"
                value={mitigationForm.reason}
                onChange={handleMitigationFormChange}
                required
              />
            </div>

            <button className="dark-button" type="submit">
              Apply Manual Block
            </button>
          </form>
        </section>

        <section className="panel mitigation-summary-panel">
          <div className="panel-header">
            <h2>Summary</h2>
          </div>

          <div className="mitigation-summary-grid">
            <div className="mitigation-summary-card">
              <span>All Actions</span>
              <strong>{mitigations.length}</strong>
            </div>

            <div className="mitigation-summary-card">
              <span>Active</span>
              <strong>
                {mitigations.filter((item) => item.status === "ACTIVE").length}
              </strong>
            </div>

            <div className="mitigation-summary-card">
              <span>Released</span>
              <strong>
                {
                  mitigations.filter((item) => item.status === "RELEASED")
                    .length
                }
              </strong>
            </div>
          </div>
        </section>
      </div>

      <section className="panel mitigations-table-panel">
        <div className="panel-header">
          <h2>Mitigation Actions</h2>
          <span>{mitigations.length} item(s)</span>
        </div>

        <div className="table-scroll">
          <table className="events-table mitigations-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Source IP</th>
                <th>Status</th>
                <th>Reason</th>
                <th>TTL</th>
                <th>Created By</th>
                <th>Created At</th>
                <th>Expires At</th>
                <th>Action</th>
              </tr>
            </thead>

            <tbody>
              {mitigations.map((mitigation) => (
                <tr key={mitigation.id}>
                  <td>{mitigation.id}</td>
                  <td>{mitigation.sourceIp}</td>
                  <td>
                    <span
                      className={
                        mitigation.status === "ACTIVE"
                          ? "status-badge active-status"
                          : "status-badge released-status"
                      }
                    >
                      {mitigation.status}
                    </span>
                  </td>
                  <td className="reason-cell">{mitigation.reason}</td>
                  <td>{mitigation.ttlSeconds}s</td>
                  <td>{mitigation.createdBy}</td>
                  <td>{formatDateTime(mitigation.createdAt)}</td>
                  <td>{formatDateTime(mitigation.expiresAt)}</td>
                  <td>
                    {mitigation.status === "ACTIVE" ? (
                      <button
                        className="release-button"
                        onClick={() => handleReleaseMitigation(mitigation.id)}
                      >
                        Release
                      </button>
                    ) : (
                      <span className="muted-text">Released</span>
                    )}
                  </td>
                </tr>
              ))}

              {mitigations.length === 0 && (
                <tr>
                  <td colSpan="9" className="empty-table-message">
                    No mitigation actions yet.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
}

export default MitigationsPage;