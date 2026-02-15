console.log("Content script loaded");

function createToneSelector() {
    const select = document.createElement('select');
    select.className = 'ai-tone-selector';
    select.style.marginRight = '8px';
    select.style.borderRadius = '18px';
    select.style.backgroundColor = 'rgb(11,87,208)';
    select.style.color = '#fff'; 
    select.style.padding = '0px 16px';
    select.style.border = 'medium';
    select.style.fontSize = '.875rem';
    select.style.fontWeight = '500';
    select.style.height = '36px';
    

    const tones = [
        { value: '', label: 'None' }, 
        { value: 'professional', label: 'Professional' }, 
        { value: 'casual', label: 'Casual' }, 
        { value: 'friendly', label: 'Friendly' }
    ];
    tones.forEach (tone => {
        const option = document.createElement('option');
        option.value = tone.value;
        option.textContent = tone.label;
        select.appendChild(option);
    });
    return select;
}

function createAIButton() {
   const button = document.createElement('div');
   button.style.borderRadius = '18px';
   button.style.backgroundColor = '#0b57d0';
   button.className = 'T-I J-J5-Ji aoO v7 T-I-atl L3';
   button.style.marginRight = '8px';
   button.innerHTML = 'AI Reply';
   button.setAttribute('role','button');
   button.setAttribute('data-tooltip','Generate AI Reply');
   return button;
}

function getEmailContent() {
    const selectors = [
        '.h7',
        '.a3s.aiL',
        '.gmail_quote',
        '[role="presentation"]'
    ];
    for (const selector of selectors) {
        const content = document.querySelector(selector);
        if (content) {
            return content.innerText.trim();
        }
        return '';
    }
}


function findComposeToolbar() {
    const selectors = [
        '.btC',
        '.aDh',
        '[role="toolbar"]',
        '.gU.Up'
    ];
    for (const selector of selectors) {
        const toolbar = document.querySelector(selector);
        if (toolbar) {
            return toolbar;
        }
        return null;
    }
}

function injectButton() {
    const existingButton = document.querySelector('.ai-reply-button');
    if (existingButton) existingButton.remove();

    const toolbar = findComposeToolbar();
    if (!toolbar) {
        console.log("Toolbar not found");
        return;
    }

    console.log("Toolbar found, creating AI button and tone selector");
    const button = createAIButton();
    button.classList.add('ai-reply-button');
    const toneSelector = createToneSelector();


    button.addEventListener('click', async () => {
        try {
            button.innerHTML = 'Generating...';
            button.disabled = true;

            const emailContent = getEmailContent();
            const selectedTone = toneSelector.value;
            const response = await fetch('http://localhost:8080/api/email/generate', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    emailContent: emailContent,
                    tone: selectedTone
                })
            });

            if (!response.ok) {
                throw new Error('API Request Failed');
            }

            const generatedReply = await response.text();
            const composeBox = document.querySelector('[role="textbox"][g_editable="true"]');

            if (composeBox) {
                composeBox.focus();
                 composeBox.innerHTML = '';
                document.execCommand('insertText', false, generatedReply);
            } else {
                console.error('Compose box was not found');
            }
        } catch (error) {
            console.error(error);
            alert('Failed to generate reply');
        } finally {
            button.innerHTML = 'AI Reply';
            button.disabled =  false;
        }
    });

    toolbar.insertBefore(button, toolbar.firstChild);   
    toolbar.insertBefore(toneSelector, toolbar.firstChild); 
}

const observer = new MutationObserver((mutations) => {
    for(const mutation of mutations) {
        const addedNodes = Array.from(mutation.addedNodes);
        const hasComposeElements = addedNodes.some(node =>
            node.nodeType === Node.ELEMENT_NODE && 
            (node.matches('.aDh, .btC, [role="dialog"]') || node.querySelector('.aDh, .btC, [role="dialog"]'))
        );

        if (hasComposeElements) {
            console.log("Compose Window Detected");
            setTimeout(injectButton, 500);
        }
    }
});



observer.observe(document.body, {
    childList: true,
    subtree: true
});