function Sidebar({ activePage, setActivePage, user, onLogout }) {
  const menuItems = [
    "Dashboard",
    "Events",
    "Simulator",
    "Mitigations",
    "Reputation",
    "Audits",
    "Policy",
  ];

  return (
    <aside className="sidebar">
      <div className="brand">
        <div className="logo">TL</div>

        <div>
          <h2>THREATLENS</h2>
          <p>Threat Control Console</p>
        </div>
      </div>

      <nav className="menu">
        {menuItems.map((item) => (
          <button
            key={item}
            className={activePage === item ? "menu-item active" : "menu-item"}
            onClick={() => setActivePage(item)}
          >
            {item}
          </button>
        ))}
      </nav>

      <div className="sidebar-footer">
        <strong>{user?.role || "OPERATOR"}</strong>
        <span>{user?.email}</span>

        <button className="logout-button" onClick={onLogout}>
          Logout
        </button>
      </div>
    </aside>
  );
}

export default Sidebar;