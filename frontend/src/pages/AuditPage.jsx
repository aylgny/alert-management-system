function AuditPage({ auditLogs }) {
  const formatDateTime = (value) => {
    if (!value) {
      return "-";
    }

    return new Date(value).toLocaleString();
  };

  const getResultClass = (result) => {
    if (result === "BLOCKED") {
      return "audit-result blocked";
    }

    if (result === "SUCCESS" || result === "ALLOWED") {
      return "audit-result success";
    }

    if (result === "ALLOWED_WITH_ALERT") {
      return "audit-result warning";
    }

    return "audit-result neutral";
  };

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1>Audit Trail</h1>
          <p>
            Review operator actions, firewall decisions, and response history
          </p>
        </div>
      </div>

      <section className="panel">
        <div className="panel-header">
          <h2>Audit Logs</h2>
          <span>{auditLogs.length} item(s)</span>
        </div>

        <div className="table-scroll">
          <table className="events-table audit-table">
            <thead>
              <tr>
                <th>Time</th>
                <th>Actor</th>
                <th>Action</th>
                <th>Target</th>
                <th>Result</th>
                <th>Details</th>
              </tr>
            </thead>

            <tbody>
              {auditLogs.map((log) => (
                <tr key={log.id}>
                  <td>{formatDateTime(log.timestamp)}</td>
                  <td>{log.actor}</td>
                  <td>{log.action}</td>
                  <td>{log.target}</td>
                  <td>
                    <span className={getResultClass(log.result)}>
                      {log.result}
                    </span>
                  </td>
                  <td className="audit-details-cell">{log.details}</td>
                </tr>
              ))}

              {auditLogs.length === 0 && (
                <tr>
                  <td colSpan="6" className="empty-table-message">
                    No audit logs yet.
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

export default AuditPage;