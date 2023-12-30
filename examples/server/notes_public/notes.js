const elNotes = document.querySelector('#notes');
const elNoteTemplate = document.querySelector('templates ul[name="note"] li');

function renderNote(note) {
  const el = elNoteTemplate.cloneNode(true);
  el.querySelector('h4').innerText = 'Note #' + note.id;
  el.querySelector('p').innerText = note.content;
  return el;
}

async function addNote() {
  const inpEl = document.querySelector('.new-note input');
  const content = inpEl.value;
  const resp = await fetch('/notes/', {
    method: "POST",
    body: content
  });
  const note = await resp.json();
  inpEl.value = '';
  console.log(note);
  elNotes.prepend(renderNote(note));
}

async function getNotes() {
  const resp = await fetch("/notes");
  const notes = await resp.json();
  elNotes.innerHTML = '';
  for (const note of notes) {
    elNotes.prepend(renderNote(note));
  }
}

getNotes();