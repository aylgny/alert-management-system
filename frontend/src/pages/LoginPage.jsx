import { useState } from "react";
import axios from "axios";
import "./LoginPage.css";

function LoginPage({ onLogin }) {
  const [form, setForm] = useState({
    email: "admin@threatlens.com",
    password: "Admin123!",
  });

  const [error, setError] = useState("");

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm({
      ...form,
      [name]: value,
    });
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    setError("");

    axios
      .post("http://localhost:8080/api/auth/login", form)
      .then((response) => {
        onLogin(response.data);
      })
      .catch((error) => {
        console.error(error);
        setError("Login failed. Please check your email and password.");
      });
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <div className="login-brand-row">
          <div className="login-logo">TL</div>

          <div>
            <h1>ThreatLens</h1>
            <p>Threat Control Console</p>
          </div>
        </div>

        <div className="login-title">
          <h2>Secure Operator Login</h2>
          <p>
            Sign in to access the AI-assisted cybersecurity monitoring
            dashboard.
          </p>
        </div>

        {error && <div className="login-error">{error}</div>}

        <form onSubmit={handleSubmit} className="login-form">
          <div className="login-field">
            <label>Email</label>
            <input
              type="email"
              name="email"
              value={form.email}
              onChange={handleChange}
              required
            />
          </div>

          <div className="login-field">
            <label>Password</label>
            <input
              type="password"
              name="password"
              value={form.password}
              onChange={handleChange}
              required
            />
          </div>

          <button type="submit">Sign In</button>
        </form>

        <div className="login-hint">
          <span>Demo Admin</span>
          <strong>admin@threatlens.com / Admin123!</strong>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;