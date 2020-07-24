// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

function LoadData() {
    fetch('/login-state').then(rsp => rsp.json()).then((stat) => {
        const commentElement = document.getElementById('comment-list');
        const loginElement = document.getElementById('login-link');
        if (stat.state)
        {
            commentElement.classList.remove('hidden');
            loginElement.href = stat.url;
            loginElement.innerHTML = "logout link";
        }
        else
        {
            loginElement.href = stat.url;
            loginElement.innerHTML = "login link";
        }
    });

    fetch('/blobstore-upload-url')
        .then(rsp => rsp.text())
        .then((imageUploadUrl) => {
            const messageForm = document.getElementById('image-upload');
            messageForm.action = imageUploadUrl;
            messageForm.classList.remove('hidden');
        });

    fetch('/data').then(response => response.json()).then((stats) => {
        const statsListElement = document.getElementById('comment-container');

        statsListElement.innerHTML = '';
        stats.forEach((comment) => {
            statsListElement.appendChild(
                createCommentElement(comment));
        })
    });
}

function createCommentElement(comment) {
    const commentElement = document.createElement('li');
    commentElement.className = 'task';

    const titleElement = document.createElement('span');
    titleElement.innerText = comment.title + "   " + comment.email;

    const deleteButtonElement = document.createElement('button');
    deleteButtonElement.innerText = 'Delete';
    deleteButtonElement.addEventListener('click', () => {
        deleteTask(comment);

        // Remove the task from the DOM.
        commentElement.remove();
    });

    commentElement.appendChild(titleElement);
    commentElement.appendChild(deleteButtonElement);
    return commentElement;
}

/** Tells the server to delete the task. */
function deleteTask(task) {
    const params = new URLSearchParams();
    params.append('id', task.id);
    fetch('/delete', {method: 'POST', body: params});
}
