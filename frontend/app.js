"use strict";

const API_BASE = window.location.origin;
const statusElement = document.querySelector("#status");
const refreshButton = document.querySelector("#refresh-button");

const state = {
    aulas: [],
    profesores: [],
    franjas: [],
    reservas: []
};

function showStatus(message, type = "success") {
    statusElement.textContent = message;
    statusElement.className = `status visible ${type}`;
}

function clearStatus() {
    statusElement.textContent = "";
    statusElement.className = "status";
}

async function request(path, options = {}) {
    const response = await fetch(`${API_BASE}${path}`, {
        ...options,
        headers: {
            ...(options.body ? { "Content-Type": "application/json" } : {}),
            ...options.headers
        }
    });

    if (!response.ok) {
        let detail = await response.text();
        try {
            const errorBody = JSON.parse(detail);
            detail = errorBody.detail || errorBody.message || errorBody.error || detail;
        } catch {
            // Keep the response text when the server does not return JSON.
        }
        const error = new Error(detail || `La API respondió con ${response.status} ${response.statusText}.`);
        error.status = response.status;
        throw error;
    }

    if (response.status === 204) return null;
    return response.json();
}

function addCell(row, value, className = "") {
    const cell = document.createElement("td");
    if (className) cell.className = className;
    cell.textContent = value ?? "—";
    row.append(cell);
    return cell;
}

function addBadge(cell, text, neutral = false) {
    const badge = document.createElement("span");
    badge.className = neutral ? "badge neutral" : "badge";
    badge.textContent = text;
    cell.append(badge);
}

function addDeleteButton(row, path, label) {
    const cell = document.createElement("td");
    const button = document.createElement("button");
    button.className = "delete-button";
    button.type = "button";
    button.textContent = "Eliminar";
    button.setAttribute("aria-label", `Eliminar ${label}`);
    button.addEventListener("click", async () => {
        if (!window.confirm(`¿Eliminar ${label}?`)) return;
        try {
            await request(path, { method: "DELETE" });
            showStatus(`${label} eliminado correctamente.`);
            await loadData(true);
        } catch (error) {
            showStatus(error.message, "error");
        }
    });
    cell.append(button);
    row.append(cell);
}

function renderTable(tableId, entries, fillRow, emptyMessage) {
    const table = document.querySelector(`#${tableId}`);
    table.replaceChildren();
    if (entries.length === 0) {
        const row = document.createElement("tr");
        const cell = document.createElement("td");
        cell.className = "empty-cell";
        cell.colSpan = table.closest("table").querySelectorAll("thead th").length;
        cell.textContent = emptyMessage;
        row.append(cell);
        table.append(row);
        return;
    }
    entries.forEach((entry) => {
        const row = document.createElement("tr");
        fillRow(row, entry);
        table.append(row);
    });
}

function setCount(id, count, label) {
    document.querySelector(`#${id}`).textContent = `${count} ${label}`;
}

function renderAulas() {
    setCount("aulas-count", state.aulas.length, state.aulas.length === 1 ? "aula" : "aulas");
    renderTable("aulas-table", state.aulas, (row, aula) => {
        addCell(row, aula.id);
        addCell(row, `${aula.capacidad} personas`);
        const equipamiento = addCell(row, "");
        addBadge(equipamiento, aula.tieneOrdenadores ? "Con ordenadores" : "Sin ordenadores", !aula.tieneOrdenadores);
        addDeleteButton(row, `/aulas/${aula.id}`, `el aula ${aula.id}`);
    }, "Todavía no hay aulas registradas.");
}

function renderProfesores() {
    setCount("profesores-count", state.profesores.length, state.profesores.length === 1 ? "profesor" : "profesores");
    renderTable("profesores-table", state.profesores, (row, profesor) => {
        addCell(row, profesor.id);
        addCell(row, profesor.nombre);
        addCell(row, (profesor.asignaturas ?? []).join(", "), "subject-cell");
        const cargo = addCell(row, "");
        const roles = [
            profesor.director ? "Director" : "",
            profesor.jefeEstudios ? "Jefe de estudios" : ""
        ].filter(Boolean);
        addBadge(cargo, roles.join(" · ") || "Docente", roles.length === 0);
        addDeleteButton(row, `/profesores/${profesor.id}`, `al profesor ${profesor.nombre}`);
    }, "Todavía no hay profesores registrados.");
}

function renderFranjas() {
    setCount("franjas-count", state.franjas.length, state.franjas.length === 1 ? "franja" : "franjas");
    renderTable("franjas-table", state.franjas, (row, franja) => {
        addCell(row, franja.id);
        addCell(row, franja.dia);
        addCell(row, `${franja.horaInicio} – ${franja.horaFin}`);
        addDeleteButton(row, `/franjashorarias/${franja.id}`, `la franja ${franja.id}`);
    }, "Todavía no hay franjas registradas.");
}

function renderReservas() {
    setCount("reservas-count", state.reservas.length, state.reservas.length === 1 ? "reserva" : "reservas");
    renderTable("reservas-table", state.reservas, (row, reserva) => {
        addCell(row, reserva.id);
        addCell(row, reserva.profesor?.nombre ?? `Profesor ${reserva.profesor?.id ?? "—"}`);
        addCell(row, reserva.aula ? `Aula ${reserva.aula.id} · ${reserva.aula.capacidad} plazas` : "—");
        const franja = reserva.franjaHoraria;
        addCell(row, franja ? `${franja.dia} · ${franja.horaInicio}–${franja.horaFin}` : "—");
        addDeleteButton(row, `/reservas/${reserva.id}`, `la reserva ${reserva.id}`);
    }, "Todavía no hay reservas registradas.");
}

function fillSelect(selectName, entries, labelFor, emptyLabel) {
    const select = document.querySelector(`#reserva-form select[name="${selectName}"]`);
    select.replaceChildren();
    const placeholder = document.createElement("option");
    placeholder.value = "";
    placeholder.textContent = entries.length ? "Selecciona una opción" : emptyLabel;
    placeholder.disabled = true;
    placeholder.selected = true;
    select.append(placeholder);

    entries.forEach((entry) => {
        const option = document.createElement("option");
        option.value = entry.id;
        option.textContent = labelFor(entry);
        select.append(option);
    });
    select.disabled = entries.length === 0;
}

function renderReservationOptions() {
    fillSelect("profesorId", state.profesores, (profesor) => `${profesor.nombre} · #${profesor.id}`, "Crea primero un profesor");
    fillSelect("aulaId", state.aulas, (aula) => `Aula #${aula.id} · ${aula.capacidad} plazas`, "Crea primero un aula");
    fillSelect("franjaId", state.franjas, (franja) => `#${franja.id} · ${franja.dia} · ${franja.horaInicio}–${franja.horaFin}`, "Crea primero una franja");
    document.querySelector('#reserva-form button[type="submit"]').disabled =
        state.profesores.length === 0 || state.aulas.length === 0 || state.franjas.length === 0;
}

async function loadData(preserveStatus = false) {
    refreshButton.disabled = true;
    try {
        const [aulas, profesores, franjas, reservas] = await Promise.all([
            request("/aulas"),
            request("/profesores"),
            request("/franjashorarias"),
            request("/reservas")
        ]);
        state.aulas = aulas;
        state.profesores = profesores;
        state.franjas = franjas;
        state.reservas = reservas;
        renderAulas();
        renderProfesores();
        renderFranjas();
        renderReservas();
        renderReservationOptions();
        if (!preserveStatus) clearStatus();
    } catch (error) {
        showStatus(`No se pudieron cargar los datos. Comprueba que Spring Boot está iniciado. ${error.message}`, "error");
    } finally {
        refreshButton.disabled = false;
    }
}

async function submitForm(form, path, makeBody, successMessage) {
    const submitButton = form.querySelector('button[type="submit"]');
    submitButton.disabled = true;
    try {
        await request(path, { method: "POST", body: JSON.stringify(makeBody(new FormData(form))) });
        form.reset();
        showStatus(successMessage);
        await loadData(true);
    } catch (error) {
        showStatus(error.message, "error");
        if (form.id === "profesor-form") {
            const role = new FormData(form).get("cargo");
            const message = error.status === 409
                ? `Ya existe ${role === "director" ? "un director" : "un jefe de estudios"}. No se puede crear otro.`
                : error.status === 400
                    ? "No se puede asignar más de un cargo a la misma persona."
                    : error.message;
            window.alert(message);
        }
    } finally {
        submitButton.disabled = false;
    }
}

document.querySelectorAll(".tab").forEach((tab) => {
    tab.addEventListener("click", () => {
        document.querySelectorAll(".tab").forEach((item) => item.classList.toggle("active", item === tab));
        document.querySelectorAll(".panel").forEach((panel) => {
            panel.classList.toggle("active", panel.id === `panel-${tab.dataset.tab}`);
        });
    });
});

document.querySelector("#aula-form").addEventListener("submit", (event) => {
    event.preventDefault();
    submitForm(event.currentTarget, "/aulas", (form) => ({
        capacidad: Number(form.get("capacidad")),
        tieneOrdenadores: form.has("tieneOrdenadores")
    }), "Aula creada correctamente.");
});

document.querySelector("#profesor-form").addEventListener("submit", (event) => {
    event.preventDefault();
    submitForm(event.currentTarget, "/profesores", (form) => ({
        nombre: form.get("nombre").trim(),
        asignaturas: form.get("asignaturas").split(",").map((subject) => subject.trim()).filter(Boolean),
        director: form.get("cargo") === "director",
        jefeEstudios: form.get("cargo") === "jefeEstudios"
    }), "Profesor creado correctamente.");
});

document.querySelector("#franja-form").addEventListener("submit", (event) => {
    event.preventDefault();
    const form = event.currentTarget;
    const values = new FormData(form);
    const from = values.get("horaInicio");
    const until = values.get("horaFin");
    if (from >= until) {
        showStatus("La hora de finalización debe ser posterior a la de inicio.", "error");
        return;
    }
    submitForm(form, "/franjashorarias", () => ({
        dia: values.get("dia"),
        horaInicio: `${from}:00`,
        horaFin: `${until}:00`
    }), "Franja horaria creada correctamente.");
});

document.querySelector("#reserva-form").addEventListener("submit", (event) => {
    event.preventDefault();
    submitForm(event.currentTarget, "/reservas", (form) => ({
        profesor: { id: Number(form.get("profesorId")) },
        aula: { id: Number(form.get("aulaId")) },
        franjaHoraria: { id: Number(form.get("franjaId")) }
    }), "Reserva creada correctamente.");
});

refreshButton.addEventListener("click", loadData);
loadData();
