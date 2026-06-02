function ReputationPage({ reputationEntries }) {
  const getStatusClass = (status) => {
    if (status === "MALICIOUS") {
      return "reputation-badge malicious";
    }

    if (status === "SUSPICIOUS") {
      return "reputation-badge suspicious";
    }

    return "reputation-badge monitored";
  };

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1>Reputation</h1>
          <p>Review suspicious source scores and threat history</p>
        </div>
      </div>

      <section className="panel">
        <div className="panel-header">
          <h2>Source Reputation</h2>
          <span>{reputationEntries.length} item(s)</span>
        </div>

        <div className="table-scroll">
          <table className="events-table reputation-table">
            <thead>
              <tr>
                <th>Source IP</th>
                <th>Event Count</th>
                <th>Max Risk</th>
                <th>Status</th>
                <th>Observed Event Types</th>
              </tr>
            </thead>

            <tbody>
              {reputationEntries.map((entry) => (
                <tr key={entry.sourceIp}>
                  <td>{entry.sourceIp}</td>
                  <td>{entry.eventCount}</td>
                  <td>{entry.maxRiskScore}</td>
                  <td>
                    <span className={getStatusClass(entry.reputationStatus)}>
                      {entry.reputationStatus}
                    </span>
                  </td>
                  <td className="event-types-cell">
                    {entry.eventTypes && entry.eventTypes.length > 0
                      ? entry.eventTypes.join(", ")
                      : "-"}
                  </td>
                </tr>
              ))}

              {reputationEntries.length === 0 && (
                <tr>
                  <td colSpan="5" className="empty-table-message">
                    No reputation entries yet.
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

export default ReputationPage;