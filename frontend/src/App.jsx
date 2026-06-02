import { useEffect, useState } from "react";
import axios from "axios";
import "./App.css";

import Sidebar from "./components/Sidebar";
import Header from "./components/Header";
import DashboardPage from "./pages/DashboardPage";
import EventsPage from "./pages/EventsPage";
import SimulatorPage from "./pages/SimulatorPage";
import MitigationsPage from "./pages/MitigationsPage";
import PlaceholderPage from "./pages/PlaceholderPage";
import LoginPage from "./pages/LoginPage";
import AuditPage from "./pages/AuditPage";
import ReputationPage from "./pages/ReputationPage";
import PolicyPage from "./pages/PolicyPage";

function App() {
  const savedToken = localStorage.getItem("token");
  const savedUser = localStorage.getItem("user");

  const [token, setToken] = useState(savedToken);
  const [user, setUser] = useState(savedUser ? JSON.parse(savedUser) : null);

  const [activePage, setActivePage] = useState("Dashboard");
  const [summary, setSummary] = useState(null);
  const [alerts, setAlerts] = useState([]);
  const [severityData, setSeverityData] = useState([]);
  const [mitigations, setMitigations] = useState([]);
  const [auditLogs, setAuditLogs] = useState([]);
  const [reputationEntries, setReputationEntries] = useState([]);
  const [policies, setPolicies] = useState([]);
  const [error, setError] = useState("");

  const [simulatorForm, setSimulatorForm] = useState({
    sourceIp: "8.8.8.8",
    username: "aylin",
    password: "normalpass123",
  });

  const [simulatorResponse, setSimulatorResponse] = useState(null);

  const [logForm, setLogForm] = useState({
    sourceIp: "",
    targetIp: "",
    eventType: "FAILED_LOGIN",
    sourceSystem: "",
    rawMessage: "",
  });

  const [mitigationForm, setMitigationForm] = useState({
    sourceIp: "",
    reason: "",
    ttlSeconds: 300,
  });

  useEffect(() => {
    if (token) {
      fetchDashboardData(token);
      fetchMitigations(token);
      fetchAuditLogs(token);
      fetchReputation(token);
      fetchPolicies(token);
    }
  }, [token]);

  const authHeaders = (currentToken = token) => {
    return {
      headers: {
        Authorization: `Bearer ${currentToken}`,
      },
    };
  };

  const handleLogin = (authData) => {
    localStorage.setItem("token", authData.token);

    const loggedInUser = {
      email: authData.email,
      role: authData.role,
    };

    localStorage.setItem("user", JSON.stringify(loggedInUser));

    setToken(authData.token);
    setUser(loggedInUser);
    setError("");
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");

    setToken(null);
    setUser(null);
    setSummary(null);
    setAlerts([]);
    setSeverityData([]);
    setMitigations([]);
    setAuditLogs([]);
    setReputationEntries([]);
    setPolicies([]);
    setSimulatorResponse(null);
    setActivePage("Dashboard");
  };

  const fetchDashboardData = (currentToken = token) => {
    axios
      .get("http://localhost:8080/api/dashboard/summary", authHeaders(currentToken))
      .then((response) => {
        setSummary(response.data);
      })
      .catch((error) => {
        console.error(error);
        setError("Dashboard verisi alınamadı.");
      });

    axios
      .get("http://localhost:8080/api/alerts", authHeaders(currentToken))
      .then((response) => {
        setAlerts(response.data);
      })
      .catch((error) => {
        console.error(error);
        setError("Alert verisi alınamadı.");
      });

    axios
      .get(
        "http://localhost:8080/api/dashboard/severity-distribution",
        authHeaders(currentToken)
      )
      .then((response) => {
        setSeverityData(response.data);
      })
      .catch((error) => {
        console.error(error);
        setError("Severity verisi alınamadı.");
      });
  };

  const fetchMitigations = (currentToken = token) => {
    axios
      .get("http://localhost:8080/api/mitigations", authHeaders(currentToken))
      .then((response) => {
        setMitigations(response.data);
      })
      .catch((error) => {
        console.error(error);
        setError("Mitigation verisi alınamadı.");
      });
  };

  const fetchAuditLogs = (currentToken = token) => {
    axios
      .get("http://localhost:8080/api/audits", authHeaders(currentToken))
      .then((response) => {
        setAuditLogs(response.data);
      })
      .catch((error) => {
        console.error(error);
        setError("Audit log verisi alınamadı.");
      });
  };

  const fetchReputation = (currentToken = token) => {
    axios
      .get("http://localhost:8080/api/reputation", authHeaders(currentToken))
      .then((response) => {
        setReputationEntries(response.data);
      })
      .catch((error) => {
        console.error(error);
        setError("Reputation verisi alınamadı.");
      });
  };

  const fetchPolicies = (currentToken = token) => {
    axios
      .get("http://localhost:8080/api/policies", authHeaders(currentToken))
      .then((response) => {
        setPolicies(response.data);
      })
      .catch((error) => {
        console.error(error);
        setError("Policy verisi alınamadı.");
      });
  };

  const handleStatusChange = (alertId, newStatus) => {
    axios
      .patch(
        `http://localhost:8080/api/alerts/${alertId}/status`,
        newStatus,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "text/plain",
          },
        }
      )
      .then((response) => {
        const updatedAlert = response.data;

        const updatedAlerts = alerts.map((alert) => {
          if (alert.id === updatedAlert.id) {
            return updatedAlert;
          }

          return alert;
        });

        setAlerts(updatedAlerts);
        fetchAuditLogs();
      })
      .catch((error) => {
        console.error(error);
        setError("Alert status güncellenemedi.");
      });
  };

  const handleLogFormChange = (event) => {
    const { name, value } = event.target;

    setLogForm({
      ...logForm,
      [name]: value,
    });
  };

  const handleLogSubmit = (event) => {
    event.preventDefault();

    axios
      .post("http://localhost:8080/api/logs/ingest", logForm, authHeaders())
      .then((response) => {
        const createdAlert = response.data;

        setAlerts([...alerts, createdAlert]);

        setLogForm({
          sourceIp: "",
          targetIp: "",
          eventType: "FAILED_LOGIN",
          sourceSystem: "",
          rawMessage: "",
        });

        fetchDashboardData();
        fetchReputation();
      })
      .catch((error) => {
        console.error(error);
        setError("Log gönderilemedi.");
      });
  };

  const handleMitigationFormChange = (event) => {
    const { name, value } = event.target;

    setMitigationForm({
      ...mitigationForm,
      [name]: value,
    });
  };

  const handleMitigationSubmit = (event) => {
    event.preventDefault();

    const payload = {
      sourceIp: mitigationForm.sourceIp,
      reason: mitigationForm.reason,
      ttlSeconds: Number(mitigationForm.ttlSeconds),
    };

    axios
      .post("http://localhost:8080/api/mitigations", payload, authHeaders())
      .then((response) => {
        setMitigations([response.data, ...mitigations]);

        setMitigationForm({
          sourceIp: "",
          reason: "",
          ttlSeconds: 300,
        });

        fetchMitigations();
        fetchAuditLogs();
      })
      .catch((error) => {
        console.error(error);
        setError("Mitigation oluşturulamadı.");
      });
  };

  const handleReleaseMitigation = (id) => {
    axios
      .delete(`http://localhost:8080/api/mitigations/${id}`, authHeaders())
      .then(() => {
        fetchMitigations();
        fetchAuditLogs();
      })
      .catch((error) => {
        console.error(error);
        setError("Mitigation release edilemedi.");
      });
  };

  const handleBlockSourceFromAlert = (alert) => {
    setMitigationForm({
      sourceIp: alert.sourceIp,
      ttlSeconds: 300,
      reason: `Mitigation requested from alert #${alert.id} (${alert.eventType}) - ${alert.title}`,
    });

    setActivePage("Mitigations");
  };

  const handleSimulatorChange = (event) => {
    const { name, value } = event.target;

    setSimulatorForm({
      ...simulatorForm,
      [name]: value,
    });
  };

  const handleSimulatorPreset = (preset) => {
    if (preset === "NORMAL") {
      setSimulatorForm({
        sourceIp: "8.8.8.8",
        username: "aylin",
        password: "normalpass123",
      });
    }

    if (preset === "SQL_INJECTION") {
      setSimulatorForm({
        sourceIp: "91.44.12.8",
        username: "admin' OR '1'='1",
        password: "123456",
      });
    }

    if (preset === "XSS") {
      setSimulatorForm({
        sourceIp: "203.0.113.55",
        username: "<script>alert('xss')</script>",
        password: "test12345",
      });
    }

    if (preset === "ADMIN_LOGIN") {
      setSimulatorForm({
        sourceIp: "185.23.45.10",
        username: "admin",
        password: "123",
      });
    }

    if (preset === "BLOCKED_IP") {
      setSimulatorForm({
        sourceIp: "45.88.12.90",
        username: "normaluser",
        password: "normalpass123",
      });
    }

    setSimulatorResponse(null);
  };

  const handleSimulatorSubmit = (event) => {
    event.preventDefault();

    axios
      .post("http://localhost:8080/api/protected/login", simulatorForm)
      .then((response) => {
        setSimulatorResponse(response.data);
        fetchDashboardData();
        fetchAuditLogs();
        fetchReputation();
      })
      .catch((error) => {
        if (error.response && error.response.data) {
          setSimulatorResponse(error.response.data);
          fetchDashboardData();
          fetchAuditLogs();
          fetchReputation();
          return;
        }

        console.error(error);
        setError("Protected API request gönderilemedi.");
      });
  };

  const handlePolicyModeChange = (policy, newMode) => {
    const payload = {
      mode: newMode,
      riskScore: policy.riskScore,
      description: policy.description,
    };

    axios
      .put(
        `http://localhost:8080/api/policies/${policy.eventType}`,
        payload,
        authHeaders()
      )
      .then((response) => {
        const updatedPolicy = response.data;

        const updatedPolicies = policies.map((item) => {
          if (item.eventType === updatedPolicy.eventType) {
            return updatedPolicy;
          }

          return item;
        });

        setPolicies(updatedPolicies);
        fetchAuditLogs();
      })
      .catch((error) => {
        console.error(error);
        setError("Policy güncellenemedi.");
      });
  };

  const renderPage = () => {
    if (activePage === "Dashboard") {
      return (
        <DashboardPage
          summary={summary}
          severityData={severityData}
          alerts={alerts}
        />
      );
    }

    if (activePage === "Events") {
      return (
        <EventsPage
          alerts={alerts}
          handleStatusChange={handleStatusChange}
          logForm={logForm}
          handleLogFormChange={handleLogFormChange}
          handleLogSubmit={handleLogSubmit}
          handleBlockSourceFromAlert={handleBlockSourceFromAlert}
        />
      );
    }

    if (activePage === "Simulator") {
      return (
        <SimulatorPage
          simulatorForm={simulatorForm}
          simulatorResponse={simulatorResponse}
          handleSimulatorChange={handleSimulatorChange}
          handleSimulatorPreset={handleSimulatorPreset}
          handleSimulatorSubmit={handleSimulatorSubmit}
        />
      );
    }

    if (activePage === "Mitigations") {
      return (
        <MitigationsPage
          mitigations={mitigations}
          mitigationForm={mitigationForm}
          handleMitigationFormChange={handleMitigationFormChange}
          handleMitigationSubmit={handleMitigationSubmit}
          handleReleaseMitigation={handleReleaseMitigation}
        />
      );
    }

    if (activePage === "Reputation") {
      return <ReputationPage reputationEntries={reputationEntries} />;
    }

    if (activePage === "Audits") {
      return <AuditPage auditLogs={auditLogs} />;
    }

    if (activePage === "Policy") {
      return (
        <PolicyPage
          policies={policies}
          handlePolicyModeChange={handlePolicyModeChange}
        />
      );
    }

    return null;
  };

  if (!token) {
    return <LoginPage onLogin={handleLogin} />;
  }

  return (
    <div className="layout">
      <Sidebar
        activePage={activePage}
        setActivePage={setActivePage}
        user={user}
        onLogout={handleLogout}
      />

      <main className="main-content">
        <Header />

        {error && <p className="error">{error}</p>}

        {renderPage()}
      </main>
    </div>
  );
}

export default App;