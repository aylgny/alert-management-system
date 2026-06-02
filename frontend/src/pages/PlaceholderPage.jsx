function PlaceholderPage({ title, description }) {
  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1>{title}</h1>
          <p>{description}</p>
        </div>
      </div>

      <section className="panel empty-panel">
        <div className="empty-circle"></div>
        <h3>No data yet</h3>
        <p>This module will be connected to the Spring Boot backend next.</p>
      </section>
    </div>
  );
}

export default PlaceholderPage;