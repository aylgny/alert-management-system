function Header() {
  return (
    <header className="topbar">
      <div>
        <h2>ThreatLens Command Center</h2>
        <p>AI-assisted cybersecurity monitoring and control</p>
      </div>

      <div className="api-pill">Admin API: 127.0.0.1:8080</div>
    </header>
  );
}

export default Header;