// Fetch and display members
async function fetchMembers() {
    const response = await fetch('http://your-backend-url/api/members');
    const members = await response.json();

    const tableBody = document.querySelector('#membersTable tbody');
    tableBody.innerHTML = ''; // Clear existing rows

    members.forEach(member => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${member.id}</td>
            <td>${member.name}</td>
            <td>${member.email}</td>
            <td>${member.clubName}</td>
            <td>${member.role}</td>
        `;
        tableBody.appendChild(row);
    });
}

// Add a new member
document.getElementById('addMemberForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const member = {
        name: document.getElementById('name').value,
        email: document.getElementById('email').value,
        clubName: document.getElementById('clubName').value,
        role: document.getElementById('role').value
    };

    await fetch('http://your-backend-url/api/members', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(member)
    });

    fetchMembers(); // Refresh the list
});

// Load members when the page loads
window.onload = fetchMembers;